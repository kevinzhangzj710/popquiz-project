package cn.edu.njust.hearth.popquiz_backend;

import cn.edu.njust.hearth.popquiz_backend.mapper.DBMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PopquizBackendApplication {

	@Autowired
	void setDBMapper1(DBMapper dbMapper) {
		dbMapper.createUserTable();
	}
	@Autowired
	void setDBMapper2(DBMapper dbMapper) {
		dbMapper.createCourseTable();
	}
	@Autowired
	void setDBMapper3(DBMapper dbMapper) {
		dbMapper.createCourseofListenerTable();
	}
	@Autowired
	void setDBMapper4(DBMapper dbMapper) {
		dbMapper.createSpeechesTable();
	}

	public static void main(String[] args) {
		SpringApplication.run(PopquizBackendApplication.class, args);
	}

}
