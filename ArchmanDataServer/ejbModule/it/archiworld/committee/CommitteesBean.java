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
package it.archiworld.committee;

import it.archiworld.common.committee.Committee;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class CommitteesBean implements Committees {

	@PersistenceContext
	private EntityManager manager;

	@SuppressWarnings("unchecked")
	public final List<Committee> searchCommitteeList(final String pattern)
		throws Throwable {
		try {
			long time = System.currentTimeMillis();
			Query query = manager
					.createQuery("Select committee from Committee as committee "
							+ "where lower(committee.name) like '"
							+ pattern.toLowerCase()
							+ "%' "
							+ "order by committee.name");
			List<Committee> result = query.getResultList();
			System.out
					.println("Person: " + (time - System.currentTimeMillis()));
			return result;
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	public final Committee saveCommittee(final Committee committee)
			throws Throwable {
		try {
			return manager.merge(committee);
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	public Committee getCommittee(Committee committee) throws Throwable {
		try {
			System.out.println("Fetching Committee");
			Query query = manager
					.createQuery("Select committee from Committee as committee where "
							+ "committee.id = '"
							+ committee.getCommittee_id()
							+ "'");
			return (Committee) query.getSingleResult();
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}
}
