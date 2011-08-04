/*******************************************************************************
 * Copyright (C) 2008  CS-Computer.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     CS-Computer - initial API and implementation
 ******************************************************************************/
package it.archiworld.protocol;

import it.archiworld.common.protocol.Document;
import it.archiworld.common.protocol.Entry;
import it.archiworld.common.protocol.Inentry;
import it.archiworld.common.protocol.Outentry;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class ProtocolBean implements Protocol {

	@PersistenceContext
	private EntityManager manager;

	public final void removeEntry(final Entry entry) throws Throwable {
		try {
			manager.remove(entry);
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	public final Entry saveEntry(final Entry entry) throws Throwable {
		try {
			if(entry.getRegistration_date() == null) entry.setRegistration_date(new Timestamp(System.currentTimeMillis()));
			if (entry.getYear() == null) entry.setYear(new GregorianCalendar().get(Calendar.YEAR));
			if (entry.getProtocol() == null) {
				Query query = manager
						.createNativeQuery("select max(protocol) from t_entry where year="
								+ new GregorianCalendar().get(Calendar.YEAR));
				Integer max_id = (Integer) query.getSingleResult();
				if (max_id != null)
					entry.setProtocol(max_id + 1);
				else
					entry.setProtocol(1);
				entry.setYear(new GregorianCalendar().get(Calendar.YEAR));
			}
			System.out.println("*###* Number of Documents:"+entry.getDocuments().size());
			for (Document doc : entry.getDocuments())
				System.out.println(doc);
			return manager.merge(entry);
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	@SuppressWarnings({"unchecked"})
	public final List<Entry> getEntryList(String searchpattern) throws Throwable {
		String where=null;
		List<String> archive=new ArrayList<String>();
		String archivecode="";
		String pattern="";
		if(Pattern.compile("^([0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{4}|\\d{4})(\\s\\w/(\\w{1,3}|\\w{1,3}/\\w*)?)+$").matcher(searchpattern).matches()){
			String[] result = Pattern.compile("\\s").split(searchpattern);
			if(result[0].length()==4)
				where=" and entry.year='"+result[0]+"'";
			else {
				String[] date = Pattern.compile("\\.").split(result[0]);
				where=" and entry.registration_date>='"+new Timestamp(new GregorianCalendar(Integer.valueOf(date[2].substring(0,4)),Integer.valueOf(date[1])-1,Integer.valueOf(date[0])).getTimeInMillis())+"' ";
			}
			if(result.length>1&&result[1]!=null){
				for(int i=1;i<result.length;i++){
					archive.add(result[i].toLowerCase());
				}
			}
		} else if(Pattern.compile("^[0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{4}(\\s.*)?$").matcher(searchpattern).matches()){
			String[] result = Pattern.compile("\\.").split(searchpattern);
			where=" and entry.year='"+result[2].substring(0,4)+"' and entry.registration_date>='"+new Timestamp(new GregorianCalendar(Integer.valueOf(result[2].substring(0,4)),Integer.valueOf(result[1])-1,Integer.valueOf(result[0])).getTimeInMillis())+"'";
			result = Pattern.compile("\\s").split(searchpattern);
			if(result.length>1&&result[1]!=null)
				pattern=result[1].toLowerCase();
		} else if(Pattern.compile("^[0-9]{1,4}/[0-9]{4}(\\s.*)?$").matcher(searchpattern).matches()) {
			String[] result = Pattern.compile("/").split(searchpattern);
			where=" and entry.protocol>="+result[0]+" and entry.year="+result[1].substring(0,4);
			result = Pattern.compile("\\s").split(searchpattern);
			if(result.length>1&&result[1]!=null)
				pattern=result[1].toLowerCase();
		} else if(Pattern.compile("^[0-9]{4}(\\s.*)?$").matcher(searchpattern).matches()) {
			where=" and entry.year="+searchpattern.substring(0,4);
			if(searchpattern.length()>5 && searchpattern.substring(5)!="")
				pattern=searchpattern.substring(5).toLowerCase();
		}
		
		if(where==null){
			where=" and entry.year="+(new GregorianCalendar().get(Calendar.YEAR));
			pattern=searchpattern;
		}

		if(!archive.isEmpty()){
			archivecode=" and (lower(entry.archive) like '"+archive.get(0).toLowerCase()+"%'";
			for(int i=1;i<archive.size();i++)
				archivecode+=" or lower(entry.archive) like '"+archive.get(i).toLowerCase()+"%'";
			archivecode+=")";
		}

		
		try {
			Long start=System.currentTimeMillis();
//			String stringquery = "Select new Inentry(entry.entry_id, entry.year, entry.protocol, entry.subject, entry.registration_date, entry.protocol_date) from Inentry as entry where (upper(entry.subject) like upper('%"+pattern+"%') or upper(entry.sender.firstname) like upper('%"+pattern+"%') or upper(entry.sender.lastname) like upper('%"+pattern+"%') or upper(entry.sender.denomination) like upper('%"+pattern+"%') or upper(entry.note) like upper('%"+pattern+"%')) "+archivecode;
			String stringquery = "Select new Inentry(entry.entry_id, entry.year, entry.protocol, entry.subject, entry.registration_date, entry.protocol_date, entry.last_change_date, entry.emergency) from Inentry as entry where (upper(entry.subject) like upper(?) or upper(entry.sender.firstname) like upper(?) or upper(entry.sender.lastname) like upper(?) or upper(entry.sender.denomination) like upper(?) or upper(entry.note) like upper(?)) "+archivecode;
			if (where!="") stringquery+=where;
			stringquery+=" order by entry.year desc,entry.protocol desc";

			Query query = manager.createQuery(stringquery).setParameter(1, "%"+pattern+"%").setParameter(2, "%"+pattern+"%").setParameter(3, "%"+pattern+"%").setParameter(4, "%"+pattern+"%").setParameter(5, "%"+pattern+"%");
			List<Inentry> result1 = query.getResultList();

			stringquery = "Select new Outentry(entry.entry_id, entry.year, entry.protocol, entry.subject, entry.registration_date, entry.protocol_date,  entry.last_change_date, entry.emergency) from Outentry as entry where (upper(entry.subject) like upper(?) or upper(entry.note) like upper(?) or entry in (Select entry2 from Entry entry2 , IN (entry2.destinations) destinations where (upper(destinations.denomination) like upper(?) or upper(destinations.lastname) like upper(?) or upper(destinations.firstname) like upper(?))))"+archivecode;
			if (where!="") stringquery+=where;
			stringquery+=" order by entry.year desc, entry.protocol desc";

			Query query2 = manager.createQuery(stringquery).setParameter(1, "%"+pattern+"%").setParameter(2, "%"+pattern+"%").setParameter(3, "%"+pattern+"%").setParameter(4, "%"+pattern+"%").setParameter(5, "%"+pattern+"%");
			List<Outentry> result2 = query2.getResultList();
				
			List<Entry> result = new ArrayList<Entry>();
			int i=0,j=0;
			while(i<result1.size() && j<result2.size()){
				if(result1.get(i).compareTo(result2.get(j))>0)
					result.add(result1.get(i++));
				else
					result.add(result2.get(j++));
			}
			if (i<result1.size()) result.addAll(result1.subList(i, result1.size()));
			if (j<result2.size()) result.addAll(result2.subList(j, result2.size()));

			System.out.println(System.currentTimeMillis()-start);
			return result;

		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}
	
	public Entry getEntry(Entry entry) throws Throwable {
		if(entry==null||entry.getEntry_id()==null)
			return null;
		try {
			Query query = manager
					.createQuery("Select entry from Entry as entry where entry.entry_id="
							+ entry.getEntry_id());
			entry = (Entry) query.getSingleResult();
			return entry;
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}
	
}

