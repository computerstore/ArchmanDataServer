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
package it.archiworld.addressbook;

import it.archiworld.common.Address;
import it.archiworld.common.Company;
import it.archiworld.common.Member;
import it.archiworld.common.Person;
import it.archiworld.common.ServiceMember;
import it.archiworld.common.protocol.Inentry;
import it.archiworld.common.protocol.Outentry;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.jboss.ejb3.entity.HibernateSession;

@Stateless
//@SecurityDomain("other")
public class AddressbookBean implements Addressbook {

	@PersistenceContext
	private EntityManager manager;

	public final void removeAddress(final Address address) throws Throwable {
		try {
			Address reattachedAddress = manager.merge(address);
			manager.remove(reattachedAddress);
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	public final Address saveAddress(final Address address) throws Throwable {
		if (address instanceof Member) {
			if (((Member) address).getRegistration_number() == null) {
				Query query = manager
						.createNativeQuery("Select max(registration_number) from t_entity");
				Integer registration_number = (Integer) query.getSingleResult();
				if (registration_number == null)
					registration_number = 0;
				((Member) address)
						.setRegistration_number(registration_number + 1);
			}
			((Member) address).setLast_change_date(new Timestamp(System
					.currentTimeMillis()));
		}
		if (address instanceof ServiceMember) {
			System.out.println("Saving Servicemember");
			if (((ServiceMember) address).getServicemember_number() == null) {
				Query query = manager
						.createNativeQuery("Select max(servicemember_number) from t_entity");
				Integer servicemember_number = (Integer) query
						.getSingleResult();
				System.out.println(servicemember_number);
				if (servicemember_number == null)
					servicemember_number = 0;
				((ServiceMember) address)
						.setServicemember_number(servicemember_number + 1);
			}
		}

		try {
			return manager.merge(address);
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	@SuppressWarnings( { "unchecked" })
	public final List<Address> getAddresslist() throws Throwable {
		try {
			Query query = manager
					.createQuery("Select address from Address as address");
			return query.getResultList();
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}

	}

	@SuppressWarnings( { "unchecked" })
	public final List<Company> searchCompanyList(final String pattern)
			throws Throwable {
		try {
			long time = System.currentTimeMillis();
			Query query = manager
					.createQuery("Select new Company(address.entity_id, address.denomination) from Company as address where upper(address.denomination) like upper(?) order by address.denomination").setParameter(1, pattern);
			List<Company> result = query.getResultList();
			System.out.println("Company: "
					+ (time - System.currentTimeMillis()));
			return result;
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	@SuppressWarnings( { "unchecked" })
	public final List<ServiceMember> searchServiceMemberList(String pattern)
			throws Throwable {
		try {
			long time = System.currentTimeMillis();
			Query query = manager
					.createQuery("Select new ServiceMember(address.entity_id, address.firstname, address.lastname, address.servicemember_number) from ServiceMember as address where (upper(address.firstname) like upper(?) OR upper(address.lastname) like upper(?)) and address.class = ServiceMember order by address.lastname").setParameter(1, pattern).setParameter(2, pattern);

			List<ServiceMember> result = query.getResultList();
			System.out.println("ServiceMember: "
					+ (time - System.currentTimeMillis()));
			return result;
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	@SuppressWarnings( { "unchecked" })
	public final List<Member> searchMemberList(final String pattern)
			throws Throwable {
		try {
			long time = System.currentTimeMillis();
			Query query = manager
					.createQuery("Select new List(address.entity_id, address.firstname, address.lastname, address.gender, address.registration_number) from Member as address where (upper(address.firstname) like upper(?) or upper(address.lastname) like upper(?)) and address.deregister_date is null order by address.lastname").setParameter(1, pattern).setParameter(2, pattern);

			List<Member> result = query.getResultList();
			List<Member> data = new ArrayList<Member>();
			for (int i = 0; i < result.size(); i++) {
				Member member = new Member((Long) ((List) result.get(i))
						.get(0), (String) ((List) result.get(i)).get(1),
						(String) ((List) result.get(i)).get(2),
						(String) ((List) result.get(i)).get(3),
						(Integer) ((List) result.get(i)).get(4));
				data.add(member);
			}
			System.out
					.println("Member: " + (time - System.currentTimeMillis()));
			return data;
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	@SuppressWarnings( { "unchecked" })
	public final List<Member> searchDeletedMemberList(final String pattern)
			throws Throwable {
		try {
			long time = System.currentTimeMillis();
			Query query = manager
					.createQuery("Select new List(address.entity_id, address.firstname, address.lastname, address.gender, address.registration_number) from Member as address where (upper(address.firstname) like upper(?) or upper(address.lastname) like upper(?)) and address.deregister_date is not null order by address.lastname").setParameter(1, pattern).setParameter(2, pattern);
			List<Member> result = query.getResultList();
			List<Member> data = new ArrayList<Member>();
			for (int i = 0; i < result.size(); i++) {
				Member member = new Member((Long) ((List) result.get(i))
						.get(0), (String) ((List) result.get(i)).get(1),
						(String) ((List) result.get(i)).get(2),
						(String) ((List) result.get(i)).get(3),
						(Integer) ((List) result.get(i)).get(4));
				data.add(member);
			}
			System.out.println("Deleted Member: "
					+ (time - System.currentTimeMillis()));

			return data;
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	@SuppressWarnings( { "unchecked" })
	public final List<Person> searchPersonList(String pattern)
			throws Throwable {
		try {
			long time = System.currentTimeMillis();
			Query query = manager
					.createQuery("Select new Person(address.entity_id, address.firstname, address.lastname) from Person as address where (upper(address.firstname) like upper(?) OR upper(address.lastname) like upper(?)) and address.class = Person order by address.lastname").setParameter(1, pattern).setParameter(2, pattern);
			List<Person> result = query.getResultList();
			System.out
					.println("Person: " + (time - System.currentTimeMillis()));
			return result;
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	public Address getAddress(Address address) throws Throwable {
		if (address == null || address.getEntity_id() == null)
			return null;
		try {
			System.out.println("Fetching Address");
			Query query = manager
					.createQuery("Select address from Address as address where address = ?").setParameter(1, address);
			return (Address) query.getSingleResult();
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	@SuppressWarnings( { "unchecked" })
	public List<Address> searchAddress(Address address) throws Throwable {
		Session session = ((HibernateSession) manager).getHibernateSession();
		Example example = Example.create(address);
		example.ignoreCase();
		example.excludeZeroes();
		example.enableLike();
		if (address instanceof Member)
			return session.createCriteria(Member.class).add(example).addOrder(
					Order.asc("lastname")).setFetchMode("renewal",
					FetchMode.SELECT).setFetchMode("specializations",
					FetchMode.SELECT).setFetchMode("formations",
					FetchMode.SELECT).setFetchMode("committeemembers", 
					FetchMode.SELECT).list();
		if (address instanceof Person) {
			List<Address> result = session.createCriteria(Person.class).add(
					example).addOrder(Order.asc("lastname")).list();
			List<Address> result2 = new ArrayList<Address>();
			for (int i = 0; i < result.size(); i++)
				if (!(result.get(i) instanceof Member)
						&& !(result.get(i) instanceof ServiceMember))
					result2.add(result.get(i));
			return result2;
		}
		if (address instanceof Company) {
			return session.createCriteria(Company.class).add(example).addOrder(
					Order.asc("denomination")).list();
		}
		return new ArrayList<Address>();
	}

	@SuppressWarnings( { "unchecked" })
	public List<Address> searchAddress(Class entityClass, Criterion criterion,
			List<Order> orders) throws Throwable {
		Session session = ((HibernateSession) manager).getHibernateSession();
		Criteria criteria = session.createCriteria(entityClass);
		if (orders.size() > 0) {
			criteria.setMaxResults(1);
			Iterator<Order> ordIt = orders.iterator();
			while (ordIt.hasNext() == true) {
				criteria.addOrder(ordIt.next());
			}
		} else {
			criteria.addOrder(Order.asc("lastname"));
		}

		criteria.add(criterion).setFetchMode("renewal", FetchMode.SELECT)
				.setFetchMode("specializations", FetchMode.SELECT)
				.setFetchMode("formations", FetchMode.SELECT)
				.setFetchMode("committeemembers", FetchMode.SELECT);
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public Member savePersonAsMember(Person person) throws Throwable {
		try {
			Query query = manager
					.createQuery("Select inentry from Inentry as inentry where inentry.sender.address=?").setParameter(1, person);
			List<Inentry> inentries = query.getResultList();
			query = manager
					.createQuery("Select outentry from Outentry outentry, IN (outentry.destinations) dest where dest.address=?").setParameter(1,person);
			List<Outentry> outentries = query.getResultList();
			Member member = new Member(person);
			
			if (member.getRegistration_number() == null) {
				query = manager
						.createNativeQuery("Select max(registration_number) from t_entity");
				Integer registration_number = (Integer) query.getSingleResult();
				if (registration_number == null)
					registration_number = 0;
				member.setRegistration_number(registration_number + 1);
			}
			member.setLast_change_date(new Timestamp(System.currentTimeMillis()));
			
			member = manager.merge(member);
			for (Inentry entry : inentries) {
				if (entry.getSender().getAddress().equals(
						person)) {
					entry.getSender().setAddress(member);
					
				}
				entry = manager.merge(entry);
			}
			for (Outentry entry : outentries)
				for (int i = 0; i < entry.getDestinations().size(); i++) {
					if (entry.getDestinations().get(i).getAddress().equals(
							person)) {
						entry.getDestinations().get(i).setAddress(member);
					}
					entry = manager.merge(entry);
					System.out
							.println("********************************************");
				}

			person = manager.merge(person);
			manager.remove(person);
			return member;
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	@SuppressWarnings("unchecked")
	public ServiceMember savePersonAsServiceMember(Person person)
			throws Throwable {
		try {
			Query query = manager
					.createQuery("Select inentry from Inentry as inentry where inentry.sender.address=?").setParameter(1,person);
			List<Inentry> inentries = query.getResultList();
			query = manager
					.createQuery("Select outentry from Outentry outentry, IN (outentry.destinations) dest where dest.address=?").setParameter(1, person);
			List<Outentry> outentries = query.getResultList();
			ServiceMember member = new ServiceMember(person);
			if (member.getServicemember_number() == null) {
				query = manager.createNativeQuery("Select max(servicemember_number) from t_entity");
				Integer servicemember_number = (Integer) query.getSingleResult();
				System.out.println(servicemember_number);
				if (servicemember_number == null)
					servicemember_number = 0;
				member.setServicemember_number(servicemember_number + 1);
			}

			member = manager.merge(member);
			for (Inentry entry : inentries) {
				if (entry.getSender().getAddress().equals(
						person)) {
					entry.getSender().setAddress(member);
				}
				entry = manager.merge(entry);
			}
			for (Outentry entry : outentries)
				for (int i = 0; i < entry.getDestinations().size(); i++) {
					if (entry.getDestinations().get(i).getAddress().equals(
							person)) {
						entry.getDestinations().get(i).setAddress(member);
					}
					entry = manager.merge(entry);
				}

			person = manager.merge(person);
			manager.remove(person);
			return member;
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

}
