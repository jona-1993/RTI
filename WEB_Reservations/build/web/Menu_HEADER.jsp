<%@page import="java.util.ArrayList"%>
<%@page import="caddie.Panier"%>
<%@page import="caddie.Calculable"%>
<%@page import="items.Chambre"%>
<%@page import="messages.Message"%>
<jsp:useBean id="Panier" scope="session" class="caddie.Panier" />
<jsp:useBean id="listeChambres" scope="session" class="java.util.ArrayList" />
<link rel="stylesheet" type="text/css" href="bootstrap/css/bootstrap.min.css" />
<link rel="stylesheet" type="text/css" href="font-awesome/css/font-awesome.min.css" />
<script type="text/javascript" src="js/jquery-1.10.2.min.js"></script>
<script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>