<?php
	
	class Database{

		function __construct() {}

		//I am assuming that this function will not return any error but it might in future and i have to take care of that too. just remember to do that
		function connect(){

			include_once './db_config.php';

			// Create connection
			$conn = mysql_connect(DB_HOST,DB_USER,MYSQL_PASSWORD) or die("Couldn't establish connection to db host".$DB_HOST);

			//select the database to work on
			$db = mysql_select_db(DB_NAME) or die('Database '.DB_NAME.' not exists on host '.DB_HOST);

		}//end of function connect

		//what is user changes his device? this case is not coverd in below code. work on it too.
		function register_user($user_phone_number, $user_device_id, $user_reg_id,$name){

			//check if device exists
			if (device_exists($user_device_id) > 0) {
				
				//if device exists then update data against it.
				if (update($user_phone_number,$user_device_id,$user_reg_id,$name) >0){
					$response = array('success' => 1 ,
										'operation' => 'USER_UPDATION');
				}else{
					$response  = array('success' => 0 ,
										'operation' => 'USER_UPDATION');
				}

				return $response;
			}else{

				//if user is inserted successfully then return response to client
				if (insert($user_phone_number,$user_device_id,$user_reg_id,$name) >0 ){

					$response = array('success' => 1 ,
										'operation' => 'NEW_DEVICE');
				}else{
					$response = array('success' => 0,
										'operation' => 'NEW_DEVICE');
				}


				return $response;
			}
		}

		function mark_published($event_id){
			$query = "update event set `published` = '1' where `event_id` = '$event_id'";
			$result = mysql_query($query);

			if ($result) {
				return 1;
			}else{
				return 0;
			}
		}

		function get_event($event_id){
			$get_event_query = "select * from event where `event_id` = '$event_id'";

			$result = mysql_query($get_event_query);

			if ($result) {
				return mysql_fetch_assoc($result);
			}
		}

		function get_all_events(){
			$query = "select * from event where `published` = 0";

			$result = mysql_query($query);

			return $result;
		}

		function get_reg_ids(){
			$ids = array();
			$select_ids = "select user_reg_id from app_user";

			$result = mysql_query($select_ids);

			while ($row = mysql_fetch_assoc($result)) {
				$ids[] = $row['user_reg_id'];
			}
			return $ids;
		}

		function save_event($event_title,$event_date,$event_address,$event_cat,$event_desc,$event_image,$published){
			$save_event_query = "insert into event (`event_id`, `attribute1`, `attribute2`, `attribute3`, `attribute4`,`attribute5`, `description`,`published`) values ('','$event_title','$event_date','$event_address','$event_cat','$event_image','$event_desc','$published')";
 			$result = mysql_query($save_event_query);

 			if ($result) {
 				return mysql_insert_id();
 			}else{
 				return -1;
 			}
		}

	}//end of class Database
	
	function device_exists($user_device_id){

			$result = mysql_query("select * from app_user where user_device_id = '".$user_device_id."'");

			if (mysql_num_rows($result) > 0){
				return 1;
			}else{
				return 0;
			}
	}


	function insert($user_phone_number,$user_device_id,$user_reg_id,$name){

		$insert_query = "insert into app_user(`user_phone_number`,`user_device_id`,`user_reg_id`,`name`) values('$user_phone_number','$user_device_id','$user_reg_id','$name')";

		$result = mysql_query($insert_query);
		if ($result) {
			return 1;
		}else{
			return 0;
		}
	}

	function update($user_phone_number,$user_device_id,$user_reg_id,$name){
		$update_query = "update app_user set `user_phone_number` = '$user_phone_number' , `user_reg_id` = '$user_reg_id' , `name` = '$name' where `user_device_id` = '$user_device_id' ";

		$result = mysql_query($update_query);
		if ($result) 
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}

?>