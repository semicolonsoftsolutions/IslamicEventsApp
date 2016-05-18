<?php
	include './gcm.php';
	include './database.php';

	if (isset($_SESSION['event_id'])){
		$event_id = $_SESSION['event_id'];
		$gcm = new Gcm();

		$db = new Database();
		$db -> connect();
		$reg_ids = $db ->get_reg_ids();


		$gcm -> push_message($reg_ids, array("msg" => 'event_id'));
	}else{
		echo 'hello ';
	}
	
	
?>