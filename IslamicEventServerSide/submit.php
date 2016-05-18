<?php
	include './database.php';

	if (isset($_POST['event_title']) &&
		isset($_POST['event_cat']) &&
		isset($_POST['event_address']) &&
		isset($_POST['event_date']) &&
		isset($_POST['event_image']) &&
		isset($_POST['event_desc']) ){

		//upload image into folder
		$image_base = $_POST['event_image'];
		$image_name = $_POST['event_title'];
		upload_image($image_base,$image_name);

		$event_title = $_POST['event_title'];
		$event_cat = $_POST['event_cat'];
		$event_address = $_POST['event_address'];
		$event_date = $_POST['event_date'];
		$event_desc = $_POST['event_desc'];

		$db = new Database();
		$db -> connect();
		$result = $db -> save_event($event_title,$event_date,$event_address,$event_cat,$event_desc,'event_images/'.$image_name.'.png',$published);
		if ($result>0) {
			echo json_encode(array('success' => 1 , 'operation'=>'EVENT_UPLOAD'));
		}else{
			echo json_encode(array('success' => 0 , 'operation'=>'EVENT_UPLOAD'));
		}

		

	}

	function upload_image($image_base,$image_name){
		$image_binary = base64_decode($image_base);

		header('Content-Type: bitmap; charset=utf-8');
    	$file = fopen('event_images/'.$image_name.'.png', 'wb');
    	fwrite($file, $image_binary);
    	fclose($file);
	}
?>