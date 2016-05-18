<?php

	include './database.php';

	 if ( isset($_POST['user_phone_no']) && 
	 	 isset($_POST['user_device_id']) && 
	 	 isset($_POST['gcm_reg_id'] )&& 
	 	 isset($_POST['name'])
	 	 ){
	 	$user_phone_no = $_POST['user_phone_no'];
	 	$user_device_id = $_POST['user_device_id'];
	 	$user_reg_id = $_POST['gcm_reg_id'];
	 	$name = $_POST['name'];

	 	$db = new Database();
	 	$db -> connect();
	 	$response = $db -> register_user($user_phone_no,$user_device_id,$user_reg_id,$name);

	 	echo json_encode($response);
	 }


?>