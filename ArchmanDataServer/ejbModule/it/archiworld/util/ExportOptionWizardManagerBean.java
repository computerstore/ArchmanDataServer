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

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ExportOptionWizardManagerBean implements ExportOptionWizardManager {
	
	@PersistenceContext
	private EntityManager manager;

	/* (non-Javadoc)
	 * @see it.archiworld.util.ExportOptionWizardManager#getMemberFieldExportSelectorList()
	 */
	@SuppressWarnings("unchecked")
	public List<MemberFieldExportSelector> getMemberFieldExportSelectorList(){
		return manager.createQuery("Select mfes from MemberFieldExportSelector mfes order by name").getResultList();
	}
	
	/* (non-Javadoc)
	 * @see it.archiworld.util.ExportOptionWizardManager#saveMemberFieldExportSelector(it.archiworld.util.MemberFieldExportSelector)
	 */
	public void saveMemberFieldExportSelector(MemberFieldExportSelector mfes){
		manager.merge(mfes);
	}
	
	/* (non-Javadoc)
	 * @see it.archiworld.util.ExportOptionWizardManager#removeMemberFieldExportSelector(it.archiworld.util.MemberFieldExportSelector)
	 */
	public void removeMemberFieldExportSelector(MemberFieldExportSelector mfes){
		manager.remove(mfes);
	}

}
