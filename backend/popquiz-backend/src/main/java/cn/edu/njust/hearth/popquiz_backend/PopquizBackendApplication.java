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
	@Autowired
	void setDBMapper5(DBMapper dbMapper) {
		dbMapper.createQUESTIONSTable();
	}
	@Autowired
	void setDBMapper6(DBMapper dbMapper) {
		dbMapper.createQUESTION_COMMENTS_Table();
	}
	@Autowired
	void setDBMapper7(DBMapper dbMapper) {
		dbMapper.createSUBMITSTable();
	}
	@Autowired
	void setDBMapper8(DBMapper dbMapper) {
		dbMapper.createPEECH_FILES_Table();
	}
	@Autowired
	void setDBMapper9(DBMapper dbMapper) {
		dbMapper.createSPEECH_COMMENTS_Table();
	}
	@Autowired
	void setDBMapper10(DBMapper dbMapper) {dbMapper.createSPEECHCONTENTTable();}

	public static void main(String[] args) {
		SpringApplication.run(PopquizBackendApplication.class, args);
	}

}
