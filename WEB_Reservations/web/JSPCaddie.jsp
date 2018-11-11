<%-- 
    Document   : JSPCaddie
    Created on : 26-oct.-2018, 13:52:28
    Author     : jona1993
--%>



<%@page import="java.text.SimpleDateFormat"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="icon" type="image/png" href="images/icons/favicon.ico"/>
        
        <%@include file="Menu_HEADER.jsp" %>
        
        <link href="css/templatemo_style.css" rel="stylesheet" type="text/css" />
        <link rel="stylesheet" type="text/css" href="css/ddsmoothmenu.css" />
        <script type="text/javascript" src="js/jquery.min.js"></script>
        <script type="text/javascript" src="js/ddsmoothmenu.js">
        </script>

        <script language="javascript" type="text/javascript">
        function clearText(field)
        {
            if (field.defaultValue == field.value) field.value = '';
            else if (field.value == '') field.value = field.defaultValue;
        }
        </script>

        <script type="text/javascript">

        ddsmoothmenu.init({
                mainmenuid: "top_nav", //menu DIV id
                orientation: 'h', //Horizontal or vertical menu: Set to "h" or "v"
                classname: 'ddsmoothmenu', //class added to menu's outer DIV
                //customtheme: ["#1c5a80", "#18374a"],
                contentsource: "markup" //"markup" or ["container_id", "path_to_menu_file"]
        })

        </script>
        
        <link rel="stylesheet" type="text/css" media="all" href="css/jquery.dualSlider.0.2.css" />

        <script src="js/jquery-1.3.2.min.js" type="text/javascript"></script>
        <script src="js/jquery.easing.1.3.js" type="text/javascript"></script>
        <script src="js/jquery.timers-1.2.js" type="text/javascript"></script>
        <title>Réservation de chambres</title>
    </head>
    <body>
            <%@include file="Menu.jsp" %>
            <div id="templatemo_wrapper">
            <div id="templatemo_header">

            <div id="site_title">
                    <h1><a href="/Reservations/ServletReservations?Action=Accueil">INPRES - Réservations</a></h1>
            </div>

            <div id="header_right">
                    <a href="/Reservations/ServletReservations?Action=Accueil">Accueil</a> | <a href="/Reservations/ServletReservations?Action=Disconnect">Se déconnecter</a>            
                    </div>

            <div class="cleaner"></div>
            </div> <!-- END of templatemo_header -->

            <div id="templatemo_menu">
                <div id="top_nav" class="ddsmoothmenu">
                    <ul>
                        <li><a href="/Reservations/ServletReservations?Action=Accueil">Accueil</a></li>
                        <li><a class="selected">Website MAP</a>
                            <ul>
                                <li><a href="/Reservations/ServletReservations?Action=Accueil">Accueil</a></li>
                                <li><a href="/Reservations/ServletReservations?Action=Panier">Panier</a></li>
                          </ul>
                        </li>
                    </ul>
                    <br style="clear: left" />
                </div> <!-- end of ddsmoothmenu -->
                <div id="menu_second_bar">
                        <div id="top_shopping_cart">
                            Dans le panier: <strong> ${Panier.getItems().size()} éléments</strong> ( <a href="/Reservations/ServletReservations?Action=Panier">Voir le Panier</a> )
                    </div>
                        <div id="templatemo_search">
                        <form action="ServletReservations?Action=Accueil" method="get">
                          <input type="text" value="Search" name="keyword" id="keyword" title="keyword" onfocus="clearText(this)" onblur="clearText(this)" class="txt_field" />
                          <input type="submit" name="Search" value=" Search " alt="Search" id="searchbutton" title="Search" class="sub_btn"  />
                        </form>
                    </div>
                    <div class="cleaner"></div>
                </div>
            </div> <!-- END of templatemo_menu -->
            
            <div id="templatemo_main">
                <div id="content">
                    <h7>Logements disponnibles: <br><br> </h7>
                    <%SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy"); 
                      for(Chambre chambre : (ArrayList<Chambre>)listeChambres) {%>
                        <form action="ServletReservations?Action=AddPanier&ID=<%out.print(chambre.getNumero());%>&EQUIP=<%out.print(chambre.getEquipement());%>&NBOCC=<%out.print(chambre.getNboccupants());%>&PRIX=<%out.print(chambre.getPrixHTVA());%>&CAT=<%out.print(chambre.getCategorie());%>&TYPE=<%out.print(chambre.getType());%>" method="post">
                            <div class="product_box">
                                <a href="productdetail.html"><img src="images/product/01.jpg" alt="Image 01" /></a>
                                <h3>Numéro: <%out.print(chambre.getNumero());%> - <%out.print(chambre.getCategorie());%> - <%out.print(chambre.getType());%></h3>
                                <p class="product_price">Prix(HTVA): <%out.print(chambre.getPrixHTVA());%> €</p>
                                <h3> Date de début:  <br><br><input name="DateDebut" value="<%out.print(f.format(new java.util.Date()));%>" </h3> </input> <br>
                                <h3> Date de fin:  <br><br><input name="DateFin" value="<%out.print(f.format(new java.util.Date()));%>" </input> </h3> <br><br>
                                <button type="submit" name="param" value="Ajouter au panier" class="add_to_card">Ajouter au panier</button>
                                <a href="" class="detail"><%if(chambre.getPrixHTVA() > 1000){ out.print("Excellent");} else {if(chambre.getPrixHTVA() < 60){ out.print("pauvres");} else {out.print("raisonnable");}}%> </a>
                            </div> 
                        </form>
                    <%}%>
                </div> 
                <div class="cleaner"></div>
            </div> <!-- END of templatemo_main -->
           
            <div id="templatemo_footer">
                <p>
                        <a href="/Reservations/ServletReservations?Action=Accueil">Accueil</a> | <a href="products.html">Products</a> 
                </p>
                
                Copyright © 2048 <a href="/Reservations/ServletReservations?Action=Accueil">INPRES</a>
            </div> <!-- END of templatemo_footer -->

        </div> <!-- END of templatemo_wrapper -->
    </body>
</html>
