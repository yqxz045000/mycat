package com.caiyi.demo.wrsplit;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 读写分离
 * 
 * @author ls
 * @2019年2月18日
 */
@RestController
@RequestMapping("/wrsplit")
public class WRSplit {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@RequestMapping("/read.do")
	public List<Map<String, Object>> read() {
		String sql = "select * from user";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> map : list) {
			Set<Entry<String, Object>> entries = map.entrySet();
			if (entries != null) {
				Iterator<Entry<String, Object>> iterator = entries.iterator();
				while (iterator.hasNext()) {
					Entry<String, Object> entry = (Entry<String, Object>) iterator.next();
					Object key = entry.getKey();
					Object value = entry.getValue();
					System.out.println(key + ":" + value);
				}
			}
		}
		return list;
	}
	
	@RequestMapping("/read2.do")
	public List<Map<String, Object>> read2() {
		String sql = "select * from test";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> map : list) {
			Set<Entry<String, Object>> entries = map.entrySet();
			if (entries != null) {
				Iterator<Entry<String, Object>> iterator = entries.iterator();
				while (iterator.hasNext()) {
					Entry<String, Object> entry = (Entry<String, Object>) iterator.next();
					Object key = entry.getKey();
					Object value = entry.getValue();
					System.out.println(key + ":" + value);
				}
			}
		}
		
		return list;
	}
	
	
	@RequestMapping("/add.do")
	public void add() {
		String name = "TName"+ new Random().nextInt(100);
		int age =  new Random().nextInt(50); 
		String sql =  "insert into user(name,age) values(\'"+name+"\',"+age+");" ;
		jdbcTemplate.execute(sql);
		System.out.println("插入成功");
	}
	
	@RequestMapping("/addAndReadNoTx.do")
	public List<Map<String, Object>> addAndReadNoTx() {
		String name = "TName"+ new Random().nextInt(100);
		int age =  new Random().nextInt(50); 
		String sql =  "insert into user(name,age) values(\'"+name+"\',"+age+");" ;
		jdbcTemplate.execute(sql);
		System.out.println("插入成功");
		
		String sql2 = "select * from user";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql2);
		for (Map<String, Object> map : list) {
			Set<Entry<String, Object>> entries = map.entrySet();
			if (entries != null) {
				Iterator<Entry<String, Object>> iterator = entries.iterator();
				while (iterator.hasNext()) {
					Entry<String, Object> entry = (Entry<String, Object>) iterator.next();
					Object key = entry.getKey();
					Object value = entry.getValue();
					System.out.println(key + ":" + value);
				}
			}
		}
		return list;	
	}
	
	@RequestMapping("/addAndReadAndTX.do")
	public List<Map<String, Object>> addAndReadAndTX() throws SQLException {
		
		TransactionSynchronizationManager.initSynchronization();
		DataSource dataSource = jdbcTemplate.getDataSource();
		Connection connection = DataSourceUtils.getConnection(dataSource);
		connection.setAutoCommit(false);
		 
		String name = "TName"+ new Random().nextInt(100);
		int age =  new Random().nextInt(50); 
		String sql =  "insert into user(name,age) values(\'"+name+"\',"+age+");" ;
		
		String sql2 = "select * from user";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql2);
		if(list!=null && list.size()<4) {
			
			jdbcTemplate.execute(sql);
			System.out.println("开启事务后，如果有写操作则全部走写库，插入成功,sql:"+sql);
			//开启事务后，如果有写操作则全部走写库，插入成功,sql:insert into user(name,age) values('TName58',0);
		}else {
			System.out.println("开启事务后，查询走的读库");
		}
			
		connection.commit();
		return list;	
	}
	
	
	

}
