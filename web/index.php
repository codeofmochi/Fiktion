<?php
	session_start();
	require_once('php/token.php');

	$header = '
		<div class="mid">
			<div class="box header">
				<div class="logo">
					<a href="#top"><img src="img/logo.png" alt="logo"/></a>
				</div>
				<div class="menu">
					<ul>
						<li><a href="#top">Home</a></li>
						<li><a href="#about">About</a></li>
						<li><a href="#features">Features</a></li>
						<li><a href="#team">The team</a></li>
						<li><a href="#contact">Contact</a></li>
						<li><a href="#download" class="border-white">Download app</a></li>
					</ul>
				</div>
			</div>
		</div>
	';

	$error = false;
	$sentError = false;
	$submit = false;
	$token = createNewToken();

	if(isset($_POST['submit'])) $submit = true;

	if($submit) {
		$lastname = filter_var($_POST['lastname'], FILTER_SANITIZE_STRING);
		$firstname = filter_var($_POST['firstname'], FILTER_SANITIZE_STRING);
		$email = filter_var($_POST['email'], FILTER_SANITIZE_EMAIL);
		$subject = filter_var($_POST['subject'], FILTER_SANITIZE_STRING);
		$msg = filter_var($_POST['message'], FILTER_SANITIZE_STRING);
		$sentToken = filter_var($_POST['sentToken'], FILTER_SANITIZE_STRING);
	}

	if (empty($lastname) || empty($firstname) || empty($email) || empty($subject) || empty($msg)) $error = true;

    if (empty($sentToken) || !tokenIsValid($sentToken)) {
      $sentError = true;
    }

	if (!$error && !$sentError) {
		$message = '
			<p>New message from fiktion.io</p>
			<p>&nbsp;</p>
			<p>Last name : '.$lastname.'</p>
			<p>First name : '.$firstname.'</p>
			<p>Email : '.$email.'</p>
			<p>Subject : '.$subject.'</p>
			<p>Message : '.$msg.'</p>
		';

		require 'php/PHPMailerAutoload.php';

		$mail = new PHPMailer;

		$mail->CharSet = 'UTF-8';
		$mail->setFrom($email, $firstname, $lastname);
		$mail->addAddress('info@fiktion.io');
		$mail->isHTML(true);

		$mail->Subject = 'New message from Fiktion.io : '.$subject;
		$mail->Body = $message;

		if(!$mail->send()) {
			$sentError = true;
		}
	}
?>

<!--
FIKTION web page
(c) 2017 Fiktion Team
EPFL CS-305 Software engineering project
@author : Alexandre CHAU
-->

<!doctype html>
<html>

<head>
	<title>Fiktion : the app where imagination meets reality</title>
	<link rel="stylesheet" type="text/css" href="css/style.css">
	<link rel="stylesheet" type="text/css" href="css/animate.css">
	<link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
	<link href="https://fonts.googleapis.com/css?family=Open+Sans:300,400" rel="stylesheet">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link rel="icon" type="image/x-icon" href="favicon.ico">
</head>

<body>
	<header class="bg-white">
		<?php echo $header ?>
	</header>

	<div id="main">
		<a id="menuHideToggle"></a>

		<div class="wrap bg-tokyo">
			<a id="top"></a>
			<?php echo $header ?>

			<div class="mid">
				<div class="box phone">
					<img src="img/screenshot-home.png" class="img-phone" alt="screenshot" />
				</div>
				<div class="box phone-desc margin-vertical bg-black-transparent wow fadeInRight">
					<div class="box padded">
						<h1 class="white">FIKTION</h1>
						<h2 class="white">The android app where imagination meets reality</h2>
						<p class="white justified">
							FIKTION brings a new way to travel and discover the world right in your pocket : it's an app dedicated to finding and travelling to real life locations of landmarks used in your favorite works of fiction! FIKTION is your perfect companion when sightseeing abroad.
						</p>

						<p class="white justified">
							FIKTION is an android app made as a student project for the EPFL CS-305 EPFL Software Engineering course.
						</p>
					</div>
				</div>
			</div>
		</div>

		<div class="wrap">
			<a id="about"></a>
			<div class="mid">
				<div class="box phone-desc expand-fast">
					<div class="box padded justified margin-vertical wow fadeInLeft">
						<h1>What is Fiktion ?</h1>
						<p>
							Find and travel to real life locations of landmarks used in your favorite works of fiction! Discover film making locations, landscapes described in books and places accurately drawn in animes.
						</p>
						<p>
							Whether you're an avid fan of Game of Thrones™ flying to Scotland, an enthusiast otaku visiting Tokyo, or just a tourist curious to find places where local popular culture magic happened, FIKTION will help you experience new adventures during your trips!
						</p>
						<p>
							Visit modern cultural legacy, compare movies, books, animations and illustrations to the very real thing! Meet people travelling for your common passion, add places from unheralded works, rate your visits and build your very own real-life fictional journey! Don't forget to brag and add pictures, update your story and add new references as they come out!
						</p>
					</div>


				</div>

				<div class="box phone expand-fast">
					<img src="img/poipage.png" class="img-fullwidth margin-vertical" alt="screenshot"/>
				</div>
			</div>
		</div>

		<div class="wrap bg-lightgray">
			<a id="features"></a>
			<div class="mid">
				<div id="feature-phone-anim" class="box third-phone margin-vertical wow bounceInLeft">
					<img id="feature-phone" src="img/nearby.png" class="img-phone" alt="features" />
				</div>
				<div class="box two-third wow fadeIn" data-wow-duration="2s">
					<div class="padded margin-vertical">
						<h1 class="black">App features</h1>

						<div id="feature-selector">
							<div id="feature-column">
								<div id="gps" class="item selected" onclick="feature($(this))">
									<p><i class="material-icons">my_location</i><span class="fttext"> GPS-based discovery</span></p>
								</div>
								<div id="content" class="item" onclick="feature($(this))">
									<p><i class="material-icons">create</i><span class="fttext"> User-driven content</span></p>
								</div>
								<div id="photo" class="item" onclick="feature($(this))">
									<p><i class="material-icons">camera_alt</i><span class="fttext"> Original pictures</span></p>
								</div>
								<div id="search" class="item" onclick="feature($(this))">
									<p><i class="material-icons">search</i><span class="fttext"> Text search</span></p>
								</div>
								<div id="profile" class="item" onclick="feature($(this))">
									<p><i class="material-icons">person</i><span class="fttext"> Rewarding user profile</span></p>
								</div>
								<div id="social" class="item" onclick="feature($(this))">
									<p><i class="material-icons">question_answer</i><span class="fttext"> Social groups</span></p>
								</div>
								<div id="design" class="item" onclick="feature($(this))">
									<p><i class="material-icons">android</i><span class="fttext"> Sleek Material Design</span></p>
								</div>
							</div>

							<div id="feature-text">
								<div class="box padded center" id="feature-desc">
									<i class="material-icons" style="font-size: 100px">my_location</i>
									<div class="box justified">
										<p>Discover nearby spots using your current position. Maybe your local shop had a movie filmed there, or take out your phone while travelling abroad and discover new places to visit!</p>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="wrap">
			<a id="team"></a>
			<div class="mid">
				<div class="box full">
					<div class="padded">
						<h1>Meet the team</h1>
						<p>FIKTION is a student project for the CS-305 Software Engineering course at EPFL. The goal was to build a complete Android app in teams of 6 people, using modern techniques of software development.</p>

						<p>Read more at <a href="#">the dev blog</a> to learn more about the technology and the process behind FIKTION.</p>

						<p>&nbsp;</p>

						<div class="box full">
							<div class="box half member wow fadeInLeft">
								<a href="https://github.com/dialexo" target="_blank">
									<img src="img/person.png" alt="member" />
									<p class="name">Alexandre Chau</p>
									<p class="position">UX design, front-end &amp; web</p>
									<p class="github">@dialexo</p>
								</a>
							</div>
							<div class="box half member wow fadeInRight">
								<a href="https://github.com/Painguin" target="_blank">
									<img src="img/person.png" alt="member" />
									<p class="name">Pedro Da Cunha</p>
									<p class="position">Database back-end</p>
									<p class="github">@Painguin</p>
								</a>
							</div>
							<div class="box half member wow fadeInRight">
								<a href="https://github.com/Rudra92" target="_blank">
									<img src="img/person.png" alt="member" />
									<p class="name">Rodrigo Soares Granja</p>
									<p class="position">Users back-end</p>
									<p class="github">@Rudra92</p>
								</a>
							</div>
							<div class="box half member wow fadeInLeft">
								<a href="https://github.com/Moodoo245" target="_blank">
									<img src="img/person.png" alt="member" />
									<p class="name">Justinas Sukaitis</p>
									<p class="position">Usability &amp; front-end</p>
									<p class="github">@Moodoo245</p>
								</a>
							</div>
							<div class="box half member wow fadeInLeft">
								<a href="https://github.com/Jostoph" target="_blank">
									<img src="img/person.png" alt="member" />
									<p class="name">Christoph Rueff</p>
									<p class="position">Code coherence</p>
									<p class="github">@Jostoph</p>
								</a>
							</div>
							<div class="box half member wow fadeInRight">
								<a href="https://github.com/taskafa" target="_blank">
									<img src="img/person.png" alt="member" />
									<p class="name">Serdar Taskafa</p>
									<p class="position">Search back-end</p>
									<p class="github">@taskafa</p>
								</a>
							</div>
						</div>

						<div class="box full bg-lightgray margin-vertical-small epfl">
							<div class="padded">
								<a href="https://epfl.ch">
									<img src="img/epfl.svg" alt="epfl" />
								</a>
								<p class="box text">The FIKTION team was coached by : </p>

								<p class="box coach">
									Blagovesta Kostova
									<br />Teaching assistant
								</p>
								<p class="box coach">
									Michal Tyszkiewicz
									<br />Teaching assistant
								</p>
								<p class="box coach">
									Prof. George Candea
									<br />Course professor
								</p>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="wrap bg-primary">
			<a id="download"></a>
			<div class="mid">
				<div class="box full margin-vertical center wow rotateIn">
					<h1 class="white">Download the app</h1>
					<h2 class="white">Enough chitchat, I want to see the app >:(</h2>
					<p class="white">Thank you for your support! You can download the beta version of FIKTION by downloading the APK below (you will need to allow installation from external sources). Please keep in mind that this is still an early version. You can report any bug or feedback using our contact form below.</p>

					<p>&nbsp;</p>

					<p>
						<a class="button-white" href="bin/fiktion-0.1.apk">Download APK</a>
						<a class="button-white disabled" href="#download">Get it on Google Play (soon...&trade;)</a>
					</p>
				</div>
			</div>
		</div>

		<div class="wrap">
			<a id="contact"></a>
			<div class="mid">
				<div class="box full">
					<div class="padded">
						<h1 class="black">Contact us</h1>
						<p>Have a question, business proposal or want to greet us? Then feel free to contact us using the following form or at <a href="mailto:info@fiktion.io">info@fiktion.io</a></p>

						<?php
							if ($submit && $error) echo '<p style="color: red">Please fill in all required informations</p>';
							else if ($submit && $sentError) echo '<p style="color:red">A server error occured. Please try again later.</p>';
							else if ($submit && !$error && !$sentError) echo '<p>Thank you for your message!</p>';
						?>

						<form action="#contact" method="post" class="wow fadeIn" data-wow-duration="2s">
							<div class="box half">
								<input type="text" name="lastname" placeholder="Last name : ex. Doe" value="<?php if($submit) echo $lastname ?>" />
								<input type="text" name="firstname" placeholder="First name : ex. John" value="<?php if($submit) echo $firstname ?>" />
								<input type="email" name="email" placeholder="Email : ex. john.doe@example.com" value="<?php if($submit) echo $email ?>" />
								<input type="subject" name="subject" placeholder="Subject : ex. Your app is kinda cool"  value="<?php if($submit) echo $subject ?>"/>
							</div>
							<div class="box half">
								<textarea name="message" placeholder="Your message here..."><?php if($submit) echo $msg ?></textarea>
							</div>
							<input type="hidden" name="sentToken" value="<?php echo $token; ?>">
							<button type="submit" name="submit">Send message</button>
						</form>

					</div>
				</div>
			</div>
		</div>

		<div class="wrap bg-lightgray footer">
			<div class="mid">
				<div class="box third">
					<img src="img/logo.png" alt="logo" width="50%"/>

					<p>
						&copy; <?php echo date('Y'); ?> Fiktion, all rights reserved.
						<br />Fiktion is a production by <a href="http://epfl.ch">EPFL</a> IC students for the CS-305 Software Engineering course 2017. Copying any part is prohibited. All images, texts and designs belong to their respective owners.
					</p>

					<a href="http://epfl.ch">
						<img src="img/epfl.svg" alt="epfl" style="width: 30%" />
					</a>
				</div>

				<div class="box third right-align bottom-menu">
					<h3>Navigation</h3>
					<br />
					<a href="#top">Home</a>
					<a href="#about">About</a>
					<a href="#features">Features</a>
					<a href="#team">The team</a>
					<a href="#contact">Contact</a>
					<a href="#download">Download app</a>
				</div>

				<div class="box third right-align">
					<h3>Website design</h3>
					<p>
					Website designed with <i class="material-icons">favorite</i> and <i class="material-icons">code</i> by <br />Alexandre Chau
					<br />&nbsp;
					<br />FIKTION developer and founder of <br />
					<a href="http://quaeritur.ch">
						<img id="quaeritur-logo" src="img/quaeritur.png" alt="quaeritur" />
					</a>
					</p>
				</div>
			</div>
		</div>

	</div>
	<a id="wow-disable"></a>
</body>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script src="js/wow.min.js"></script>

<script>
	function menuShouldHide() {
		return $('#menuHideToggle').css('display') == 'none'
	}

	$(window).resize(function() {
		if (menuShouldHide()) {
			$('header').hide();
		}
	})
</script>

<script>
	if ($('#wow-disable').css('display') == 'none') {
		$('.wow').removeClass('wow fadeInLeft fadeInRight bounceInLeft');
	}

	if (menuShouldHide()) {
		$('header').hide()
	}
	else if ( $(this).scrollTop() > 100 && !$('header').hasClass('open') ) {
		$('header').addClass('open');
		$('header').slideDown();
	}

	$(window).scroll(function () {
		if (menuShouldHide()) {
			$('header').hide()
		}
		else if ( $(this).scrollTop() > 100 && !$('header').hasClass('open') ) {
			$('header').addClass('open');
			$('header').slideDown();
		}
		else if ( $(this).scrollTop() <= 100 ) {
			$('header').removeClass('open');
			$('header').slideUp();
		}
	});
</script>

<script>
	new WOW().init();
</script>

<script>
	var current = "gps"

	var ftImg = new Map()
	ftImg.set("gps", "img/nearby.png")
	ftImg.set("content", "img/contribute.png")
	ftImg.set("search", "img/search.png")
	ftImg.set("photo", "img/akihabara2.png")
	ftImg.set("profile", "img/profile.png")
	ftImg.set("social", "img/menu.png")
	ftImg.set("design", "img/login.png")

	var ftHtml = new Map()
	ftHtml.set("gps", '<i class="material-icons" style="font-size: 100px">my_location</i><div class="box justified"><p>Discover nearby spots using your current position. Maybe your local shop had a movie filmed there, or take out your phone while travelling abroad and discover new places to visit!</p></div>')
	ftHtml.set("content", '<i class="material-icons" style="font-size: 100px">create</i><div class="box justified"><p>Join a passionate community of movies, books and games enthusiasts dedicated to bring fiction into real life. Rate, review and discover places added by others. Join the hunt now, become our next best contributor and get rewarded!</p></div>')
	ftHtml.set("photo", '<i class="material-icons" style="font-size: 100px">camera_alt</i><div class="box justified"><p>Can\'t visit a place or need some previews? Browse through pictures taken by other contributors and enjoy the view just like if you were there. Compare real life pictures to movie snapshots, book descriptions or anime drawings. And if you visit the place, don\'t forget to add your very own picture!</p></div>')
	ftHtml.set("search", '<i class="material-icons" style="font-size: 100px">search</i><div class="box justified"><p>Use our fully-fledged search engine, powered by Algolia, to find your favorite locations. Want to visit somewhere? Search by city, country or place. Want to discover where your favorite movie was filmed? Search by any work of fiction, such as a movie, an anime, a book or a game</p></div>')
	ftHtml.set("profile", '<i class="material-icons" style="font-size: 100px">person</i><div class="box justified"><p>Record your real-life fictional journey through the years on your profile. Browse your likes, favorites and wishlist. Show it to your friends, or to everyone. Will you be our greatest traveller?</p></div>')
	ftHtml.set("social", '<i class="material-icons" style="font-size: 100px">question_answer</i><div class="box justified"><p>Join groups of fans of the same work or the same city, and build new relationships with people that share the same passions with you!</p></div>')
	ftHtml.set("design", '<i class="material-icons" style="font-size: 100px">android</i><div class="box justified"><p>Our philosophy : an app should be enjoyable just for its design. We strive to bring you the best and prettiest user interfaces so that you can love our app as much as we do.</p></div>')

	function feature(elem) {
		var id = elem.attr('id')
		$('#'+current).removeClass('selected')
		$('#'+id).addClass('selected')
		current = id
		$('#feature-phone').attr('src', ftImg.get(id))
		$('#feature-phone-anim').removeClass('animated bounceInLeft wow')
		$('#feature-phone-anim').addClass('animated bounceInLeft')
		$('#feature-desc').html(ftHtml.get(id))
	}
</script>

</html>
