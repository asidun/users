package juja.microservices.users.dao;

import juja.microservices.users.entity.Keeper;
import juja.microservices.users.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 * @author Olga Kulykova
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

    @Inject
    private CRMUserRepository crmUserRepository;

    @Inject
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @Before
    public void setup() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void getAllUsersCRMUserRepositoryTest() throws URISyntaxException, IOException {
        URI uri = UserRepositoryTest.class.getClassLoader().getResource("allUsers.json").toURI();
        String allUsers = new String(Files.readAllBytes(Paths.get(uri)), Charset.forName("utf-8"));

        List<User> expected = new ArrayList<>();
        expected.add(new User("AAAA123", "Vasya", "Ivanoff", "vasya@mail.ru", "vasya@gmail.com", "vasya", "vasya.ivanoff",
                        "linkedin/vasya", "facebook/vasya", "twitter/vasya"));
        expected.add(new User("AAAA456", "Kolya", "Sidoroff", "kolya@mail.ru", "kolya@gmail.com", "kolya", "kolya.sidoroff",
                "linkedin/kolya", "facebook/kolya", "twitter/kolya"));
        expected.add(new User("AAAA789", "Lena", "Petrova", "lena@mail.ru", "lena@gmail.com", "lena", "lena.petrova",
                "linkedin/lena", "facebook/lena", "twitter/lena"));

        mockServer.expect(requestTo("http://127.0.0.1/x2engine/index.php/api2/Contacts?c_isStudent=1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(allUsers, MediaType.APPLICATION_JSON));

        List<User> result = crmUserRepository.getAllUsers();

        mockServer.verify();
        assertThat(result, is(expected));
    }

    @Test
    public void searchUserBySlackTest() throws URISyntaxException, IOException {
        URI uri = UserRepositoryTest.class.getClassLoader().getResource("vasya.json").toURI();
        String mockUser = new String(Files.readAllBytes(Paths.get(uri)), Charset.forName("utf-8"));

        User expected = new User("AAAA123", "Vasya", "Ivanoff", "vasya@mail.ru", "vasya@gmail.com", "vasya",
                "vasya.ivanoff", "linkedin/vasya", "facebook/vasya", "twitter/vasya");

        mockServer.expect(requestTo("http://127.0.0.1/x2engine/index.php/api2/Contacts?c_isStudent=1&c_slack=vasya"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockUser, MediaType.APPLICATION_JSON));

        User result = crmUserRepository.getUserBySlack("vasya");
        mockServer.verify();
        assertThat(result, is(expected));
    }

    @Test
    public void searchUserByUuidTest() throws URISyntaxException, IOException {
        URI uri = UserRepositoryTest.class.getClassLoader().getResource("vasya.json").toURI();
        String mockUser = new String(Files.readAllBytes(Paths.get(uri)), Charset.forName("utf-8"));

        User expected = new User("AAAA123", "Vasya", "Ivanoff", "vasya@mail.ru", "vasya@gmail.com", "vasya",
                "vasya.ivanoff", "linkedin/vasya", "facebook/vasya", "twitter/vasya");

        mockServer.expect(requestTo("http://127.0.0.1/x2engine/index.php/api2/Contacts?c_isStudent=1&c_uuid=AAAA123"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockUser, MediaType.APPLICATION_JSON));

        User result = crmUserRepository.getUserByUuid("AAAA123");
        mockServer.verify();
        assertThat(result, is(expected));
    }

    @Test
    public void getActiveKeepersCRMUserRepositoryTest() throws URISyntaxException, IOException {
        URI uri = UserRepositoryTest.class.getClassLoader().getResource("keepers.json").toURI();
        String keepers = new String(Files.readAllBytes(Paths.get(uri)), Charset.forName("utf-8"));

        List<Keeper> expected = new ArrayList<>();
        expected.add(new Keeper("AAAA123", "description1", "Ivanoff"));
        expected.add(new Keeper("AAAA456", "description2", "Sidoroff"));
        expected.add(new Keeper("AAAA123", "description3", "Petrova"));

        mockServer.expect(requestTo("http://127.0.0.1/x2engine/index.php/api2/Keepers?c_isActive=1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(keepers, MediaType.APPLICATION_JSON));

        List<Keeper> result = crmUserRepository.getActiveKeepers();

        mockServer.verify();
        assertThat(result, is(expected));
    }

}