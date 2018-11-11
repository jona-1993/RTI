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
                    <h1><a href="/Reservations/ServletReservations?Action=Accueil">INPRES - Paiements</a></h1>
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
            <form method="post" action="ServletReservations?Action=Paiement">

                   <h7>Panier</h7>
                   <table width="680px" cellspacing="0" cellpadding="5">
                                   <tr bgcolor="#ddd">
                                   <th width="220" align="left">Image </th> 
                                   <th width="180" align="left">Description </th> 
                                   <th width="100" align="center">Nombre d'occupants </th> 
                                   <th width="60" align="right">Prix HTVA </th> 
                                   <th width="60" align="right">Prix TVAC </th> 
                                   <th width="90"> </th>
                           </tr>
                           
                           <% for(int i = 0; i < Panier.getItems().size(); i+=1) {%>
                           
                            <tr>
                                   <td><img src="images/product/01.jpg" alt="Image 01" /></td> 
                                   <td><%out.print(((items.Chambre)Panier.getItems().get(i)).getNumero());%> - <%out.print(((items.Chambre)Panier.getItems().get(i)).getType());%> : <%out.print(((items.Chambre)Panier.getItems().get(i)).getCategorie());%></td> 
                               <td align="center"><%out.print(((items.Chambre)Panier.getItems().get(i)).getNboccupants());%> </td>
                               <td align="right"><%out.print(((items.Chambre)Panier.getItems().get(i)).getPrixHTVA());%> €</td> 
                               <td align="right"><%out.print(((items.Chambre)Panier.getItems().get(i)).getPrixHTVA()*1.21);%> €</td>
                               <td align="center"> <a href="/Reservations/ServletReservations?Action=DelPanier&ID=<%out.print(((items.Chambre)Panier.getItems().get(i)).getNumero());%>&EQUIP=<%out.print(((items.Chambre)Panier.getItems().get(i)).getEquipement());%>&NBOCC=<%out.print(((items.Chambre)Panier.getItems().get(i)).getNboccupants());%>&PRIX=<%out.print(((items.Chambre)Panier.getItems().get(i)).getPrixHTVA());%>&CAT=<%out.print(((items.Chambre)Panier.getItems().get(i)).getCategorie());%>&TYPE=<%out.print(((items.Chambre)Panier.getItems().get(i)).getType());%>">Supprimer</a> </td>
                            </tr>
                           <%}%>
                           <td align="right" style="background:#ddd; font-weight:bold"> Total (TVAC) </td>
                           <td align="right" style="background:#ddd; font-weight:bold"><%out.print(Panier.getPrix()*1.21);%> € </td>
                                           </table>
                       <div style="float:right; width: 215px; margin-top: 20px;">

                       <p>
                       <h7>Numéro de carte:</h7> 
                       <h3> <input name="NumCard" type="text" </input> </h3>
                               <br><br>
                               <button name="PayerButton" value=="Payer" > </button>
                        </p>
                              
                       <p><a href="javascript:history.back()">Continuer votre shopping..</a></p>

                       </div>
                   
            </form>
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
