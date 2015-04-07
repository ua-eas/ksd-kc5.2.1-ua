package org.kuali.kra.test.helpers;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kra.test.fixtures.PersonFixture;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfo;
import org.kuali.rice.kim.impl.identity.entity.EntityBo;
import org.kuali.rice.kim.impl.identity.principal.PrincipalBo;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * A class that helps test cases create and persist a Person from a
 * PersonFixture to a DB.
 *
 */
public class PersonTestHelper extends TestHelper {
	
	public Person createPerson(PersonFixture personFixture) {

		BusinessObjectService businessObjectService = getService(BusinessObjectService.class);
		Entity entity = buildEntity(personFixture);
		Principal principal = buildPrincipal(personFixture);
		entity.getPrincipals().add(principal);
		EntityBo entityBo = EntityBo.from(entity);
		businessObjectService.save(entityBo);

		return getService(PersonService.class).getPerson(personFixture.getPrincipalId());
	}

	public void deletePerson(PersonFixture personFixture) {

		// Have to delete first, principal.getEntityId() is a FK
		Principal principal = buildPrincipal(personFixture);
		PrincipalBo principalBo = PrincipalBo.from(principal);
		getService(BusinessObjectService.class).delete(principalBo);

		Entity entity = buildEntity(personFixture);
		EntityBo entityBo = EntityBo.from(entity);
		getService(BusinessObjectService.class).delete(entityBo);
	}

	private Entity buildEntity(PersonFixture personFixture) {
		Entity.Builder entityBuilder = Entity.Builder.create();
		entityBuilder.setActive(true);
		entityBuilder.setVersionNumber(0L);
		entityBuilder.setId(personFixture.getEntityId());
		List<EntityTypeContactInfo.Builder> entityTypes = buildEntityTypeContactInfoList(personFixture);
		entityBuilder.setEntityTypes(entityTypes);
		return entityBuilder.build();
	}

	private Principal buildPrincipal(PersonFixture personFixture) {
		Principal.Builder principalBuilder = Principal.Builder.create(personFixture.getPrincipalName());
		principalBuilder.setActive(true);
		principalBuilder.setVersionNumber(0L);
		principalBuilder.setEntityId(personFixture.getEntityId());
		principalBuilder.setPrincipalId(personFixture.getPrincipalId());
		return principalBuilder.build();
	}

	private List<EntityTypeContactInfo.Builder> buildEntityTypeContactInfoList(PersonFixture personFixture) {
		List<EntityTypeContactInfo.Builder> results = new ArrayList<EntityTypeContactInfo.Builder>();
		EntityTypeContactInfo.Builder builder = EntityTypeContactInfo.Builder.create(personFixture.getEntityId(), KimConstants.EntityTypes.PERSON);
		builder.setActive(true);
		builder.setVersionNumber(0L);
		results.add(builder);
		return results;
	}

	public Person getPerson(PersonFixture personFixture) {
		return getService(PersonService.class).getPersonByPrincipalName(personFixture.getPrincipalName());
	}
}
