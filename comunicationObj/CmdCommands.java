package comunicationObj;

public class CmdCommands {
	
	/*
	 * 
	 * Insieme di comandi per rendere più ordinata la comunicazione dei pacchetti
	 *
	 */
	
	public final static String hello = "f342"; //comando di prima interazione tra client-server, per richiedere il messaggio di benvenuto del server
	public final static String gimmeUsrList = "h34g"; //comando per richiedere la lista degli utenti online
	public final static String startPlay = "243hkj"; //comando per chiedere di iniziare una sessione di gioco
	public final static String authMe = "adgf786"; //comando per autenticarsi
	public final static String notifyMyPresence = "adffg8y"; //comando per notificare la presenza dei thread
	public final static String wannaPlayWith = "gjh67"; //comando per indicare con chi si vuole giocare
	public final static String waitingForResponse = "kjnsfdg"; //comando per indicare che siamo in attesa di una risposta dall'utente che abbiamo invitato a giocare
	public final static String userBusy = "k13246sfdg"; //comando per indicare che l'utente che abbiamo invitato a giocare è già occupato
	public final static String answerYES = "YEAHAHAHAHAH"; //comando per rispondere affermativamente ad una richiesta di gioco
	public final static String answerNO = "Nope, soz"; //comando per rispondere negativamente ad una richiesta di gioco
	public final static String gameStart = "Game starting"; //comando per rispondere negativamente ad una richiesta di gioco
	public final static String setMeFree = "set me free plz"; //comando per impostare lo stato dell'utente a libero dopo che è successo un errore
	public final static String iLostTheGame = "i lost the game :(:(:("; //INVIATO DAL CLIENTTHREAD comando per indicare che chi invia il comando ha perso il gioco
	public final static String youLostTheGame = "you lost the game :(:(:("; //INVIATO DAL SERVERTHREAD comando per indicare che chi riceve il comando ha perso il gioco
	public final static String youWinTheGame = "you won the game <3"; //INVIATO DAL SERVERTHREAD comando per indicare che ha vinto il suo currentGame
	public final static String setMyFlotta = "hey,my ships are here"; //INVIATO DAL CLIENTTHREAD comando per inviare la propria flotta
	public final static String setMyFlottaOK = "i recieved your positions"; //INVIATO DAL SERVERTHREAD comando per indicare che la propria flotta è stata settata
	public final static String letsPlay = "we r playin"; //Serve per scambiare messaggi quando il game è attivo, indica che il game è al primo turno
	public final static String playing = "that's my move"; //Serve per scambiare messsaggi relativi alle mosse quando il game è iniziato
	public final static String playingOK = "i received ur move"; 
	public final static String colpito = "pezzo di nave colpito"; 
	public final static String mancato = "non hai colpito niente"; 
	public final static String setQuadRed = "set this red"; 
	public final static String setQuadGreen = "set this green"; 
	public final static String welcome = "Benvenuto nel server !!";

}
