<%-- 
    Document   : JSPInit
    Created on : 25-oct.-2018, 22:38:34
    Author     : jona1993
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:useBean id="username" scope="session" class="messages.Message"> </jsp:useBean>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="icon" type="image/png" href="images/icons/favicon.ico"/>
        <link href="//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
        <script src="//netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min.js"></script>
        <script src="//code.jquery.com/jquery-1.11.1.min.js"></script>
        <link rel="stylesheet" type="text/css" href="css/mycss.css" />
        <script type="text/javascript">
            setTimeout(function() {
                window.location = "/Reservations/ServletReservations?Action=Accueil";
            },5000);
        </script>
        <link rel="stylesheet" type="text/css" href="css/preloader.css">
        <title>Initialisation</title>
    </head>
    <br><h1> Bonjour  <jsp:getProperty name="username" property="message"/> , Vous allez être redirigé vers la page marchande...</h1><br><br><br>
    <body>
        <div class="container">
            <div class="row">
                <div class="col-md-12">
                    <div class="loader">
                        <div class="roll"></div>
                        <div class="box">
                            <div class="loader-inner">Chargement</div>
                            <div class="loader-inner">Patience</div>
                            <div class="loader-inner">please..</div>
                            <div class="loader-inner">Hello <jsp:getProperty name="username" property="message"/> </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
