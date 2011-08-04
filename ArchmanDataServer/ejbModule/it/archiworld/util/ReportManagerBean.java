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

import java.util.GregorianCalendar;
import java.util.List;

import it.archiworld.common.Member;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ReportManagerBean implements ReportManager {
	
	@PersistenceContext
	private EntityManager manager;

	/* (non-Javadoc)
	 * @see it.archiworld.util.ReportManager#getNotPaidMembers(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Member> getNotPaidMembers(final String year){
		return manager.createQuery("Select address from Member as address where address.deregister_date is null and address.registration_date<? and address not in (Select address2 from Member as address2, IN (address2.renewal) renewal where renewal.year = ?)").setParameter(1, new GregorianCalendar(Integer.parseInt(year),0,0).getTime()).setParameter(2, year).getResultList();
	}
}
