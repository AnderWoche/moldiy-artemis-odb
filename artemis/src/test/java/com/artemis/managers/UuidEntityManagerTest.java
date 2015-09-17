package com.artemis.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.UUID;

import com.artemis.WorldConfiguration;
import org.junit.Assert;
import org.junit.Test;

import com.artemis.Entity;
import com.artemis.MundaneWireException;
import com.artemis.World;

@SuppressWarnings("static-method")
public class UuidEntityManagerTest {
	
	@Test(expected=MundaneWireException.class)
	public void throw_exception_missing_uuid_manager() {
		World world = new World();
		world.initialize();
		
		Entity entity = world.createEntity();
		
		Assert.assertNotNull(entity.getUuid());
	}
	
	@Test
	public void uuid_assigned() {
		UuidEntityManager uuidManager = new UuidEntityManager();
		World world = new World(new WorldConfiguration()
				.setSystem(uuidManager));
		
		Entity entity = world.createEntity();
		
		assertNotNull(entity.getUuid());
		UUID uuid1 = entity.getUuid();
		world.deleteEntity(entity);
		
		world.process();
		world.process();
		
		entity = world.createEntity();
		
		assertNotNull(entity.getUuid());
		UUID uuid2 = entity.getUuid();

		assertNotEquals(uuid1, uuid2);
	}
	
	@Test
	public void uuid_updates_work() {
		UuidEntityManager uuidManager = new UuidEntityManager();
		World world = new World(new WorldConfiguration()
				.setSystem(uuidManager));

		Entity entity = world.createEntity();
		
		UUID uuid0 = entity.getUuid();
		Assert.assertNotNull(uuid0);
		
		UUID uuid1 = UUID.randomUUID();
		
		assertEquals(uuid0, entity.getUuid());
		entity.setUuid(uuid1);
		assertEquals(uuid1, entity.getUuid());
		
		
		assertNotEquals(uuid0, uuid1);
		assertNull(uuidManager.getEntity(uuid0));
		assertEquals(entity, uuidManager.getEntity(uuid1));
	}
	
	@Test
	public void explicit_uuids() {
		UuidEntityManager uuidManager = new UuidEntityManager();
		World world = new World(new WorldConfiguration()
				.setSystem(uuidManager));
		
		UUID[] uuids = new UUID[3];
		uuids[0] = UUID.randomUUID();
		uuids[1] = UUID.randomUUID();
		uuids[2] = UUID.randomUUID();
		
		Entity e1 = world.createEntity(uuids[0]);
		Entity e2 = world.createEntity(uuids[1]);
		Entity e3 = world.createEntity(uuids[2]);
		
		assertEquals(uuids[0], e1.getUuid());
		assertEquals(uuids[1], e2.getUuid());
		assertEquals(uuids[2], e3.getUuid());
	}

	@Test
	public void reuser_uuids_during_same_tick() {
		UUID uuid = UUID.randomUUID();

		WorldConfiguration configuration = new WorldConfiguration();
        configuration.setSystem(UuidEntityManager.class);

	    World world = new World(configuration);
	    UuidEntityManager uuidEntityManager = world.getManager(UuidEntityManager.class);
	    Entity entity = world.createEntity(uuid);
	    world.process();
	    assertEquals(0, uuidEntityManager.getEntity(uuid).id); // Entity[0]
	    world.deleteEntity(entity);
	    world.createEntity(uuid);
	    assertEquals(1, uuidEntityManager.getEntity(uuid).id); // Entity[1]
	    world.process();
	    assertEquals(1, uuidEntityManager.getEntity(uuid).id);
	}
}
