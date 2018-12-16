/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import caddie.Panier;
import database.facility.DBRequest;
import items.Chambre;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import messages.Message;
import sun.security.pkcs11.wrapper.Functions;

/**
 *
 * @author jona1993
 */
public class ServletReservations extends HttpServlet {
    
    private DBRequest connection;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    
    @Override
    public void init(ServletConfig config)
            throws ServletException {
        
        super.init(config); //To change body of generated methods, choose Tools | Templates.
        
        ServletContext sc = getServletContext();
        
        try
        {
            connection = new DBRequest("jdbc:mysql://192.168.10.100:3306/BD_HOLIDAYS?serverTimezone=UTC", "jona1993" , "azerty1234");
        }
        catch(ClassNotFoundException e)
        {
            sc.log(e.getMessage());
        }
        catch(SQLException e)
        {
            sc.log(e.getMessage());
        }
        
        sc.log("Démarrage de la servlet de contrôle");
        
    }

    @Override
    public void destroy() {
        
        
        super.destroy(); //To change body of generated methods, choose Tools | Templates.
        
        try
        {
            connection.Close();
        }
        catch(SQLException e){}
    }
    
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext sc = getServletContext();
        RequestDispatcher dispatcher;
        String idSess = null;
        HttpSession session;
        String action = request.getParameter("Action");
        ResultSet result;
        Hashtable<Integer, Object> hash = new Hashtable<>();
        ArrayList<Chambre> listeChambres;
        Panier panier = null;
        Chambre ch;
        Message message;
        
        session = request.getSession(true);
        
        sc.log("Action: " + action);
        
        
        Cookie[] tabCookies = request.getCookies();
        
        if(tabCookies != null) {
            for(int i=0; i < tabCookies.length; i+=1){
                if("idSession".equals(tabCookies[i].getName()))
                    idSess = tabCookies[i].getValue();
            }
        }
        else
            idSess = (String)session.getAttribute("session.identifier"); // Pas de cookies, je teste par suivis de session
        
        int i = 0;
        switch(action)
        {
            case "Authentication":
                // Recherche si le user/pw match dans la db
                try
                {
                    hash.put(0, request.getParameter("username"));
                    hash.put(1, request.getParameter("pass"));
                    result = connection.SelectTable("USERS", "count(*)", "login = ? and password = ?", hash);
                    result.next();
                    if(result.getInt(1) < 1)
                    {
                        response.sendRedirect("/Reservations/index.html?Error=" + URLEncoder.encode("Le login ou password est incorrect !"));
                        return;
                    }
                }
                catch(SQLException e)
                {
                    response.sendRedirect("/Reservations/index.html?Error=Erreur:" + URLEncoder.encode(e.getSQLState()));
                    return;
                }
                if(idSess == null){
                    idSess = request.getParameter("username") + "[" + session.getId() + "]";
                    session.setAttribute("session.identifier", idSess); // Je crée le suivis au cas où cookies désactivés
                    Cookie biscuit = new Cookie("idSession", idSess);
                    response.addCookie(biscuit);
                }
                session.setAttribute("Authentication", "Authenticated");
                Message username = new Message((String)request.getParameter("username"));
                session.setAttribute("username", username);
                
                message = new Message("Aucun");
                session.setAttribute("Message", message);
                
                dispatcher = sc.getRequestDispatcher("/JSPInit.jsp");

                dispatcher.forward(request, response);
                break;
            case "Accueil":
                if(IsAuthenticated(session))
                {
                    panier = (Panier)session.getAttribute("Panier");

                    if(panier == null) // Initialisation du caddie
                    {
                        panier = new Panier();
                        hash = new Hashtable<>();

                        try
                        {
                            hash.put(0, connection.getLoginIdentity(((Message)session.getAttribute("username")).getMessage()));
                            result = connection.SelectTable("CHAMBRES, RESERVATIONS", "CHAMBRES.numero, CHAMBRES.équipement, CHAMBRES.nboccupants, CHAMBRES.prixHTVA, CHAMBRES.categorie, CHAMBRES.type", "CHAMBRES.numero = RESERVATIONS.reservation and RESERVATIONS.voyageurtitulaire = ? and paye = 0", hash);
                            while(result.next()){
                                ch = new Chambre(result.getInt(1)-100, result.getString(2), result.getInt(3), result.getInt(4), result.getString(5), result.getString(6));
                                panier.addItem(ch);
                            }
                        }
                        catch(SQLException ex){
                        Logger.getLogger(ServletReservations.class.getName()).log(Level.SEVERE, null, ex);}

                        session.setAttribute("Panier", panier);
                    }

                    listeChambres = new ArrayList<Chambre>();
                    try
                    {
                        result = connection.SelectTable("CHAMBRES", "*", "", new Hashtable<>());
                        i = 1;
                        while(result.next())
                        {
                            ch = new Chambre(result.getInt(1)-100, result.getString(2), result.getInt(3), result.getInt(4), result.getString(5), result.getString(6));
                            listeChambres.add(ch);
                            i+=1;
                        }
                    }
                    catch(SQLException e)
                    {
                        sc.log(e.getSQLState());
                    }
                    catch(Exception e)
                    {
                        sc.log(e.getMessage());
                    }

                    session.setAttribute("listeChambres", listeChambres);

                    dispatcher = sc.getRequestDispatcher("/JSPCaddie.jsp");

                    dispatcher.forward(request, response);
                }
                break;
            case "AddPanier":
                if(IsAuthenticated(session))
                {
                    ch = new Chambre(Integer.parseInt(request.getParameter("ID")), request.getParameter("EQUIP"), Integer.parseInt(request.getParameter("NBOCC")), Integer.parseInt(request.getParameter("PRIX")), request.getParameter("CAT"), request.getParameter("TYPE"));
                    panier = (Panier)session.getAttribute("Panier");
                    String datedebut = request.getParameter("DateDebut");
                    String datefin = request.getParameter("DateFin");
                    SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
                    synchronized(this) // vérifier si chambre dispo a tel date etc..
                    {
                        if(panier == null)
                            panier = new Panier();

                        try
                        {
                            for(i = 0; ((Chambre)panier.getItems().get(i)).getNumero() != ch.getNumero(); i+=1); // Je recherche si le numéro n'est pas déjà dans le panier
                            message = new Message("Vous ne pouvez prendre qu'un article à la fois, payez avant ! (Sécurité contre l'abus de réservation)");
                            session.setAttribute("Message", message);
                        }
                        catch(Exception e) // C'est clair, il n'y est pas..
                        {
                            try
                            {
                                if(connection.RoomIsFull(((Message)session.getAttribute("username")).getMessage(), ch.getNumero()) < 1)
                                {
                                    if(connection.RoomIsFree(ch.getNumero(), new java.sql.Date(f.parse(datedebut).getTime()), new java.sql.Date(f.parse(datefin).getTime())) < 1)
                                    {
                                        connection.ReserverChambre(((Message)session.getAttribute("username")).getMessage(), ch.getNumero(), f.parse(datedebut), f.parse(datefin));
                                        connection.Commit();

                                        panier.addItem(ch);

                                        session.setAttribute("Panier", panier);

                                        message = new Message("Ajout au panier: succès !");
                                        session.setAttribute("Message", message);
                                    }
                                    else
                                    {
                                        message = new Message("Cette chambre est indisponnible à ce moment précis !");
                                        session.setAttribute("Message", message);
                                    }
                                }
                                else
                                {
                                    message = new Message("Vous devriez choisir une chambre plus grande !");
                                    session.setAttribute("Message", message);
                                }
                            }
                            catch(ParseException | SQLException ex)
                            {
                                try {
                                    connection.Rollback();
                                } catch (SQLException ex1) {
                                    Logger.getLogger(ServletReservations.class.getName()).log(Level.SEVERE, null, ex1);
                                }
                                message = new Message("Ajout au panier: échec - " + ex.getMessage());
                                session.setAttribute("Message", message);
                            }
                        }
                    }
                    dispatcher = sc.getRequestDispatcher("/ServletReservations?Action=Accueil");

                    dispatcher.forward(request, response);
                }
                break;
            case "DelPanier":
                if(IsAuthenticated(session))
                {
                    int idChambre = Integer.parseInt(request.getParameter("ID"));
                    panier = (Panier)session.getAttribute("Panier");

                    synchronized(this)
                    {
                        if(panier == null)
                            panier = new Panier();

                        try
                        {
                            hash = new Hashtable<>();
                            hash.put(0, idChambre + 100);
                            hash.put(1, connection.getLoginIdentity(((Message)session.getAttribute("username")).getMessage()));
                            connection.DropTable("RESERVATIONS", hash, "reservation = ? and voyageurtitulaire = ? and paye = 0");
                            connection.Commit();
                            for(i = 0; i < panier.getItems().size(); i+=1)
                            {
                                if(idChambre == ((Chambre)panier.getItems().get(i)).getNumero())
                                    panier.removeItem(panier.getItems().get(i));
                            }    

                            session.setAttribute("Panier", panier);

                            message = new Message("Retrait du panier: Succès !");
                            session.setAttribute("Message", message);
                        }
                        catch(SQLException e){
                            try {
                                connection.Rollback();
                            } catch (SQLException ex) {
                                Logger.getLogger(ServletReservations.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            message = new Message("Retrait du panier: échec - " + e.getSQLState());
                            session.setAttribute("Message", message);

                        }
                    }
                    dispatcher = sc.getRequestDispatcher("/ServletReservations?Action=Accueil");

                    dispatcher.forward(request, response);
                }
                break;
            case "Panier":
                if(IsAuthenticated(session))
                {
                    message = new Message("Aucun");
                    session.setAttribute("Message", message);

                    dispatcher = sc.getRequestDispatcher("/JSPPay.jsp");
                    dispatcher.forward(request, response);
                }
                break;
            case "Paiement":
                if(IsAuthenticated(session))
                {
                    panier = (Panier)session.getAttribute("Panier");
                    hash = new Hashtable<>();
                    if(!panier.IsEmpty())
                    {
                        try
                        {
                            panier.ApplyTVA(21); // TVA 21% sur le "Luxe" ici même si il y a du crado
                            
                            if(Payer(request.getParameter("NumCard"), panier.getPrix()))
                            {
                                hash.put(0, 1);
                                hash.put(1, connection.getLoginIdentity(((Message)session.getAttribute("username")).getMessage()));

                                if(connection.UpdateTable("paye", "RESERVATIONS", hash, "voyageurtitulaire = ? and reservation > 100") != 1)
                                {
                                    connection.Rollback();
                                    panier.PurgePanier();
                                    
                                    session.setAttribute("Panier", panier);

                                    message = new Message("Erreur de paiement, votre réservation n'existe plus !");
                                    
                                    session.setAttribute("Message", message);

                                    dispatcher = sc.getRequestDispatcher("/ServletReservations?Action=Accueil");

                                    dispatcher.forward(request, response);
                                }
                                connection.Commit();
                                panier.PurgePanier(); // Je vide mon panier

                                session.setAttribute("Panier", panier);

                                message = new Message("Paiement effectué avec succès !");
                                session.setAttribute("Message", message);

                                dispatcher = sc.getRequestDispatcher("/ServletReservations?Action=Accueil");

                                dispatcher.forward(request, response);
                            }
                            else
                            {
                                message = new Message("Le numéro de carte est invalide !");
                                session.setAttribute("Message", message);

                                dispatcher = sc.getRequestDispatcher("/JSPPay.jsp");
                                dispatcher.forward(request, response);
                            }
                        }
                        catch(SQLException e)
                        {
                            try {
                                
                                connection.Rollback();
                            } catch (SQLException ex) {
                                Logger.getLogger(ServletReservations.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            message = new Message("Une erreur s'est produite lors de votre paiement: " + e.getSQLState());
                            session.setAttribute("Message", message);

                            dispatcher = sc.getRequestDispatcher("/JSPPay.jsp");
                            dispatcher.forward(request, response);
                        } catch (MessagingException ex) {
                            try {
                                connection.Rollback();
                            } catch (SQLException ex1) {
                                Logger.getLogger(ServletReservations.class.getName()).log(Level.SEVERE, null, ex1);
                            }
                            Logger.getLogger(ServletReservations.class.getName()).log(Level.SEVERE, null, ex);
                            message = new Message("Une erreur s'est produite lors de votre paiement: " + ex.getMessage());
                            session.setAttribute("Message", message);
                        }
                    }
                    else
                    {
                        message = new Message("Paiement impossible, le panier est vide !");
                        session.setAttribute("Message", message);

                        dispatcher = sc.getRequestDispatcher("/ServletReservations?Action=Accueil");
                        dispatcher.forward(request, response);
                    }
                }
                break;
            case "Disconnect":
                if(IsAuthenticated(session))
                {
                    request.removeAttribute("username");
                    request.removeAttribute("session.identifier");
                    Cookie biscuit = new Cookie("idSession", null);
                    response.addCookie(biscuit);
                    session.invalidate();
                    dispatcher = sc.getRequestDispatcher("/index.html"); // Ajouter un message d'erreur (JavaScript?)

                    dispatcher.forward(request, response);
                }
                break;
            
        }
        
        dispatcher = sc.getRequestDispatcher("/index.html"); // Si aucun match ou pas connecté (Retour à la page de login)

        dispatcher.forward(request, response);
        
        /*response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            // TODO output your page here. You may use following sample code. 
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ServletReservations</title>");            
            out.println("</head>");
            out.println("<body>");
            
        
            if(idExist) // DEBUG SESSION
                out.println("<h1>Vous êtes reconnu par l'id: " + idSess + "</h1>");
            else
                out.println("<h1>Vous recevez l'id: " + idSess + "</h1>");
            
            out.println("<h1>Servlet ServletReservations at " + request.getContextPath() + "</h1>");
            
            out.println("<h1>Servlet Page at " + request.getServletPath() + "</h1>");
            
            out.println("</body>");
            out.println("</html>");
        }*/
    }
    
    private boolean Payer(String numcarte, int prix) throws AddressException, MessagingException {
        // Paiements
        
        Session sess;
        Properties props = new Properties();
        MimeMessage message;
        
        
        props.put("mail.smtp.host", "u2");
        props.put("file.encoding", "utf-8");
        props.put("mail.smtp.port", "25");
        
        sess = Session.getDefaultInstance(props, null);
        
        
    
        if(numcarte.length() > 5)
        {
            message = new MimeMessage(sess);
            
            message.setFrom(new InternetAddress("capitano@u2.tech.hepl.local"));
            
            InternetAddress to = new InternetAddress("capitano@u2.tech.hepl.local");
            
            message.addRecipient(javax.mail.Message.RecipientType.TO, to);
            
            
            message.setSubject("Etat de votre paiement");
            
            Multipart mp = new MimeMultipart();
            
            MimeBodyPart mbp = new MimeBodyPart();
            mbp.setText("Votre paiement a été effectué avec succès !");
            mp.addBodyPart(mbp);
            
            message.setContent(mp);
            
            
            Transport.send(message);
            
            return true;
        }
        else
            return false;
    }
    
    private boolean IsAuthenticated(HttpSession session) {
        String authstate = (String)session.getAttribute("Authentication");
        
        if(authstate != null && authstate.compareTo("Authenticated") == 0)
            return true;
        else
            return false;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
