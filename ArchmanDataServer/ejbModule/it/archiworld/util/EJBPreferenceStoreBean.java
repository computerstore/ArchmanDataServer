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
package it.archiworld.util;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;

@Stateless
public class EJBPreferenceStoreBean implements EJBPreferenceStore {

	@PersistenceContext
	private EntityManager manager;
	
	private AuditReader reader;
	
	public EJBPreferenceStoreBean() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#contains(java.lang.String)
	 */
	public boolean contains(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#getBoolean(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public boolean getBoolean(String name) {
		List result = manager.createQuery("Select ejbprefobject from EjbPreferenceObject where name=?").setParameter(1, name).getResultList();
		return !result.isEmpty();
	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#getDefaultBoolean(java.lang.String)
	 */
	public boolean getDefaultBoolean(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#getDefaultDouble(java.lang.String)
	 */
	public double getDefaultDouble(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#getDefaultFloat(java.lang.String)
	 */
	public float getDefaultFloat(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#getDefaultInt(java.lang.String)
	 */
	public int getDefaultInt(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#getDefaultLong(java.lang.String)
	 */
	public long getDefaultLong(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#getDefaultString(java.lang.String)
	 */
	public String getDefaultString(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#getDouble(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public double getDouble(String name) {
		List<Double> list = manager.createQuery("Select ejbprefobj.dvalue from EJBPreferenceObject as ejbprefobj where ejbprefobj.name=?").setParameter(1, name).getResultList();
		if(!list.isEmpty())
			return list.get(0);
		return 0;
	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#getFloat(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public float getFloat(String name) {
		List<Float> list = manager.createQuery("Select ejbprefobj.fvalue from EJBPreferenceObject as ejbprefobj where ejbprefobj.name=?").setParameter(1, name).getResultList();
		if(!list.isEmpty())
			return list.get(0);
		return 0;
	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#getInt(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public int getInt(String name) {
		List<Integer> list = manager.createQuery("Select ejbprefobj.ivalue from EJBPreferenceObject as ejbprefobj where ejbprefobj.name=?").setParameter(1, name).getResultList();
		if(!list.isEmpty())
			return list.get(0);
		return 0;
	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#getLong(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public long getLong(String name) {
		List<Long> list = manager.createQuery("Select ejbprefobj.lvalue from EJBPreferenceObject as ejbprefobj where ejbprefobj.name=?").setParameter(1, name).getResultList();
		if(!list.isEmpty())
			return list.get(0);
		return 0;
	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#getString(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public String getString(String name) {
		List<String> list = manager.createQuery("Select ejbprefobj.svalue from EJBPreferenceObject as ejbprefobj where ejbprefobj.name=?").setParameter(1, name).getResultList();
		if(!list.isEmpty())
			return list.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<String> getStringList(String name, Timestamp timestamp) {
		List<EJBPreferenceObject> list=null;
		if(timestamp!=null){
			System.out.println("Getting Stringlist "+name+" with Timestamp: "+timestamp);
			try {
				reader = AuditReaderFactory.get(manager);
				Number revision = reader.getRevisionNumberForDate(new Date(timestamp.getTime()));
				System.out.println(revision);
				list = reader.createQuery()
				.forEntitiesAtRevision(EJBPreferenceObject.class, revision)
				.add(AuditEntity.id().eq(name))
				.setMaxResults(1)
				.getResultList();
			}
			catch (Exception ex){
				ex.printStackTrace();
			}
		}
		else {
			System.out.println("Getting Stringlist without Timestamp");
			list = manager.createQuery("Select ejbprefobj from EJBPreferenceObject as ejbprefobj where ejbprefobj.name=?").setParameter(1, name).getResultList();
		}
		if(list!=null && !list.isEmpty() && list.get(0)!=null)
			return list.get(0).getListString();
		return null;
	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#putValue(java.lang.String, java.lang.String)
	 */
	public void putValue(String name, String value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#setDefault(java.lang.String, double)
	 */
	public void setDefault(String name, double value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#setDefault(java.lang.String, float)
	 */
	public void setDefault(String name, float value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#setDefault(java.lang.String, int)
	 */
	public void setDefault(String name, int value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#setDefault(java.lang.String, long)
	 */
	public void setDefault(String name, long value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#setDefault(java.lang.String, java.lang.String)
	 */
	public void setDefault(String name, String defaultObject) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#setDefault(java.lang.String, boolean)
	 */
	public void setDefault(String name, boolean value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#setToDefault(java.lang.String)
	 */
	public void setToDefault(String name) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#setValue(java.lang.String, double)
	 */
	public void setValue(String name, double value) {
		manager.merge(new EJBPreferenceObject(name,value));
	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#setValue(java.lang.String, float)
	 */
	public void setValue(String name, float value) {
		manager.merge(new EJBPreferenceObject(name,value));
	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#setValue(java.lang.String, int)
	 */
	public void setValue(String name, int value) {
		manager.merge(new EJBPreferenceObject(name,value));
	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#setValue(java.lang.String, long)
	 */
	public void setValue(String name, long value) {
		manager.merge(new EJBPreferenceObject(name,value));
	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#setValue(java.lang.String, java.lang.String)
	 */
	public void setValue(String name, String value) {
		manager.merge(new EJBPreferenceObject(name,value));
	}

	/* (non-Javadoc)
	 * @see it.archiworld.util.EJBPreferenceStore#setValue(java.lang.String, boolean)
	 */
	public void setValue(String name, boolean value) {
		manager.merge(new EJBPreferenceObject(name,value));
	}

	public void setValue(String name, List<String> value) {
		manager.merge(new EJBPreferenceObject(name,value));		
	}


}
