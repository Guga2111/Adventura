package com.luisgosampaio.adventura;

import com.luisgosampaio.adventura.domain.group.GroupController;
import com.luisgosampaio.adventura.domain.group.GroupService;
import com.luisgosampaio.adventura.domain.trip.TripController;
import com.luisgosampaio.adventura.domain.trip.TripService;
import com.luisgosampaio.adventura.domain.user.UserController;
import com.luisgosampaio.adventura.domain.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AdventuraApplicationTests {

	@Autowired
	private UserController userController;

	@Autowired
	private UserService userService;

	@Autowired
	private GroupController groupController;

	@Autowired
	private GroupService groupService;

	@Autowired
	private TripController tripController;

	@Autowired
	private TripService tripService;

	@Test
	void contextLoads() {
		assertThat(userController).isNotNull();
		assertThat(userService).isNotNull();
		assertThat(groupController).isNotNull();
		assertThat(groupService).isNotNull();
		assertThat(tripController).isNotNull();
		assertThat(tripService).isNotNull();
	}
}
