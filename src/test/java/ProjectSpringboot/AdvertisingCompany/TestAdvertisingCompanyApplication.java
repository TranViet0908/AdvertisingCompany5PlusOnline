package ProjectSpringboot.AdvertisingCompany;

import org.springframework.boot.SpringApplication;

public class TestAdvertisingCompanyApplication {

	public static void main(String[] args) {
		SpringApplication.from(AdvertisingCompanyApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
