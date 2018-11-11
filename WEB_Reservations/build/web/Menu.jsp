
<!-- Navbar Dropdown Cart - START -->

<div class="mix">
<nav class="navbar navbar-inverse">
  <div class="container-fluid">
    <div class="navbar-header menuitem">
      <a class="navbar-brand" href="/Reservations/ServletReservations?Action=Accueil">INPRES - Réservations</a>
    </div>
    <div>
      <ul class="nav navbar-nav">
        <li class="active"><a href="/Reservations/ServletReservations?Action=Accueil">Accueil</a></li>
        <li><a href="/Reservations/ServletReservations?Action=Panier">Panier</a></li>
        <li><a href="/Reservations/ServletReservations?Action=Disconnect">Se déconnecter</a></li>
        <li><a >Message du serveur:  <%out.print(((Message)session.getAttribute("Message")).getMessage());%> </a> </li>
      </ul>
    </div>
    <div>  <ul class="nav navbar-nav navbar-right">
        <li class="dropdown">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"> <span class="fa fa-gift bigicon"></span> <%out.print(Panier.getItems().size());%> - éléments dans le panier<span class="caret"></span></a>
          <ul class="dropdown-menu dropdown-cart" role="menu">
              <% for(int i = 0; i < Panier.getItems().size(); i+=1) {%>
              <form action="ServletReservations?Action=DelPanier&ID=<%out.print(((items.Chambre)Panier.getItems().get(i)).getNumero());%>&EQUIP=<%out.print(((items.Chambre)Panier.getItems().get(i)).getEquipement());%>&NBOCC=<%out.print(((items.Chambre)Panier.getItems().get(i)).getNboccupants());%>&PRIX=<%out.print(((items.Chambre)Panier.getItems().get(i)).getPrixHTVA());%>&CAT=<%out.print(((items.Chambre)Panier.getItems().get(i)).getCategorie());%>&TYPE=<%out.print(((items.Chambre)Panier.getItems().get(i)).getType());%>" method="post">
              <li>
                  <span class="item">
                    <span class="item-left">
                        <img src="http://www.prepbootstrap.com/Content/images/template/menucartdropdown/item_1.jpg" alt="" />
                        <span class="item-info">
                            <span><%out.print(((items.Chambre)Panier.getItems().get(i)).getNumero());%> : <%out.print(((items.Chambre)Panier.getItems().get(i)).getCategorie());%> <%out.print(((items.Chambre)Panier.getItems().get(i)).getType());%></span>
                            <span>Prix(HTVA): <%out.print(((items.Chambre)Panier.getItems().get(i)).getPrixHTVA());%> euros</span>
                        </span>
                    </span>
                    <span class="item-right">
                        <button class="btn btn-danger  fa fa-close"></button>
                    </span>
                </span>
              </li>
              </form>
              <%}%>
              <span>Prix Total(HTVA): <%out.print(Panier.getPrix());%> euros</span>               
              <li class="divider"></li>
              <li><a class="text-center" href="/Reservations/ServletReservations?Action=Panier">Voir le panier</a></li>
          </ul>
        </li>
      </ul>
    </div>
  </div>
</nav>
</div>

<style>
.bigicon {    
    color:white;
}

.mix{
    min-height:0px;
}

ul.dropdown-cart{
    min-width:250px;
    border: 2px solid #343434;
    padding: 2px;
    margin: 7px;
    margin-top: 11px;
}
ul.dropdown-cart li .item{
    display:block;
    padding:3px 10px;
    margin: 3px 0;
    
}
ul.dropdown-cart li .item:hover{
    background-color:#c3c5c5;
    
}
ul.dropdown-cart li .item:after{
    visibility: hidden;
    display: block;
    font-size: 0;
    content: " ";
    clear: both;
    height: 0;
}

ul.dropdown-cart li .item-left{
    float:left;
}
ul.dropdown-cart li .item-left img,
ul.dropdown-cart li .item-left span.item-info{
    float:left;
}
ul.dropdown-cart li .item-left span.item-info{
    margin-left:10px;   
}
ul.dropdown-cart li .item-left span.item-info span{
    display:block;
}
ul.dropdown-cart li .item-right{
    float:right;
}
ul.dropdown-cart li .item-right button{
    margin-top:14px;
}   
</style>
<!-- Navbar Dropdown Cart - END -->