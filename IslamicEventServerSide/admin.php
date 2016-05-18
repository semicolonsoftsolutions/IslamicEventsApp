<?php session_start(); ?>
<html>
	<head>
		<style type="text/css">
		.bar_dir{
			width: 70%;
			margin: 0 auto;
			background-color: #e5e5e5;
			text-align: center;
		}
		.msg{
			width: 70%;
			margin: 0 auto;
			background-color: #e5e5e5;
			text-align: center;
		}
		.content_div{
			width: 70%;
			margin: 0 auto;
			overflow: auto;
			background-color: #e5e5e5;
		}
		.left_div{
			float: left;
			width: 29%;
			margin: 0 auto;
		}
		.right_div{
			float: right;
			width: 69%;
			margin: 0 auto;
		}
		</style>
		<title>Broadcast Events what</title>
	</head>
	<body>
		<div class="main_div">
			<div class="bar_dir">
				<h3>Event broadcasting</h3>
			</div>
			<div class="msg">
					<?php
						include'./database.php';

						if (isset($_POST['submit'])) {
							$event_title = $_POST['event_title'];
							$event_date = $_POST['event_date'];
							$event_cat = $_POST['event_cat'];
							$event_address = $_POST['event_address'];
							$event_desc = $_POST['event_desc'];

							$db = new Database();
							$db -> connect();
							$event_id = $db -> save_event($event_title,$event_date,$event_address,$event_cat,$event_desc,0);
							


							include './gcm.php';

	
							
							$gcm = new Gcm();
							$reg_ids = $db ->get_reg_ids();

							$result = $gcm -> push_message($reg_ids, array("msg" => $db -> get_event($event_id)));

								

							
						}
					?>
			</div>
			<div class ="content_div">
				<div class="left_div">
					<table>
					<tr><td><b>New Category</b></td><td><input type="input" name="event_title"/></td></tr>
					<tr><td colspan="2"><input type="submit"></td></tr>
				</table>
				</div>
				<div class="right_div">
					<center>
						<form method="POST" action="">
							<table>
								<tr><td><b>Title</b></td><td><input type="input" name="event_title"/></td></tr>
								<tr><td><b>Date</b></td><td><input type="input" name="event_date"/></td></tr>
								<tr><td><b>Category</b></td><td><input type="input" name="event_cat"/></td></tr>
								<tr><td><b>Address</b></td><td><input type="input" name="event_address"/></td></tr>
								<tr><td><b>Description:</b></td><td><textarea name="event_desc" rows="6" cols="50"></textarea></td></tr>
								<tr><td colspan="2"><input type="submit" name= "submit" text="Broadcast"></td></tr>
							</table>
						</form>
					</center>
				</div>
			</div>
		</div>
	</body>
</html>
