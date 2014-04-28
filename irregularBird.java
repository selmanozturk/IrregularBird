
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import java.util.Scanner;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;



public class irregularBird extends JFrame{
	Timer timer1,timer2;
	int upperConduitDown,upperConduitLeft,upperConduitRight;//upper conduit's coordinates- �st borunun koordinatlar�
	int lowerConduitUp,lowerConduitLeft,lowerConduitRight;//lower conduit's cordinates - alt borunun koordinatlar�
	int birdDown,birdUp,birdRight,birdLeft;//coordinates of bird - ku�un koordinatlar�
	int groundSlideSpeed=2;//initial sliding value of ground - yerin ba�lang�� kayma h�z�
	int birdDirection=3,birdSpeedUp=3,birdSpeedDown=3;//initial speeds of bird - ku�un ba�lang�� h�zlar�
	int scoreCounter,currentMaxScore=0;
	
	JTextField playerName;//player name field - oyuncu ismi alan�
	JPanel GetPlayerNamePanel;//getting player name panel - oyuncu isminin al�naca�� panel
	
	boolean isSettingsPanelPressed;//clicking controller of settings panel - ayarlar�n bas�� kontrolc�s� 
	boolean soundOn=false;//clicking controller of sound - ses t�klama kontrolc�s�
	boolean textFieldPressed;//clicking controller of player name - oyuncu ismi t�klama kontrolc�s�
	boolean isMutePressed=false;//clicking controller of mute - ses susturma kontrolc�s�
	boolean isHighScorePressed;//clicking controller of high score label - y�ksek skor t�klama kontrolc�s�
	Clip makersClip;//sound of makers menu - yap�mc�lar men�s�n�n sesi
	
	BufferedWriter highScoreText;//high score text writer
	BufferedReader textReader;//high score text reader
	
	JPanel contentPane;//main pane - ana panel
	JPanel homePage;//main page - oyun ba�lang�� sayfas�
	JPanel gameOverPanel;//game over panel - oyun bitti sayfas�
	JPanel settingsPanelBackground,settingsPanel;//settings panel - ayarlar paneli
	JPanel highScorePanel;//high score panel - yuksek skor paneli
	
	JLabel bird;//bird - ku�
	JLabel upperConduit[],lowerConduit[];//conduits - borular
	JLabel leftGround,rightGround;//ground - yer
	JLabel scoreLabel,showScore;//current score label - anl�k skor labeli
	
	JLabel startLabel,makersLabel,settingsLabel,highScoreLabel;//main menu labels - ana men�deki labeller
	
	JLabel endMakersLabel;//makers label - yap�mc�lar labeli
	JLabel highScoreTextLabel;//high score label - y�ksek skor labeli
	String textToString = "";//string version of txt file - txt dosyas�n�n string versiyonu
	
	
	JTextArea scoresTextArea;//player name input area - oyuncu ismi girilen alan
	
	KeyListener keyListener1;//key listener for exitting from makers menu - yap�mc�lar menusunden c�k�s icin olusturdugumuz key listener
	
	//in main class we just create a irregularBird object
	//main class'da sadece bi irregularBird objesi olu�turuyoruz.
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					irregularBird frame = new irregularBird();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		);
		
		

	}
	
	public irregularBird(){
		setTitle("Irregulars Bird \u00A9 Written by Selman&Emre \u0005 All Rigths Reserved");//set a title to frame-pencere ismi
		setResizable(false);//Screen resize-ekran boyutu de�i�imi
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Close on exit-Kapat�nca kodu sonland�r
		setBounds(0,0, 1024, 768);// pencere boyutu 
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		
		
		
		contentPane.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				birdDirection = -birdSpeedUp;
				if(soundOn){
					try {
			        	javax.sound.sampled.AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/resources/cipetpet.wav").getAbsoluteFile());
			        	Clip clip = AudioSystem.getClip();
			        	clip.open(audioInputStream);
			        	clip.start();
			    	} catch(Exception ex) {
			    		System.out.println("Error with playing sound.");
			        ex.printStackTrace();
			    	}
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) 
			{
				birdDirection = birdSpeedDown;
			}
		});
		
		addHighScoreArea();
		
		addMakersLabel();
		addGameOver();
		addSettingsPanel();	
		addPauseLabel();
		addMuteLabel();
		addHomePage();
		addBird();
		addGround();
		addConduit();
		addBackground();
		
		
			timer1 = new Timer(5,new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				bird.setLocation(bird.getLocation().x, bird.getLocation().y + birdDirection);//slides the bird - ku�u kayd�r�yor
				slideConduitsAndGround();//slides conduits and the bird - borular� ve yeri kayd�r�yor
				refreshConduits();//a method of refreshing conduit - borular� kayd�ran metod
				refreshGround();//a method of refreshing ground - yeri kayd�ran metod
				scoring();//scoring method - skoru sayd�ran metod
				
				if(!collisionCheck())//collision check - �arma kontrol�
					gameOverStatus();//game over statu - oyunun bitme durumu
				
			}
		});
			
			timer2 = new Timer(5,new ActionListener() {// slides the makers label - yap�mc�lar labelini kayd�r�yor
				
				@Override
				public void actionPerformed(ActionEvent e) {
					endMakersLabel.setLocation(endMakersLabel.getLocation().x, endMakersLabel.getLocation().y-2);
					
					if(endMakersLabel.getLocation().y<-2304){
						endMakersLabel.setLocation(0,0);
					}
					
					
					
					
					
				}
			});
			
			
			addWindowListener(new WindowListener() {//writes score to text when exit - ��k��ta skoru txt dosyas�na tazd�r�yor 
				
				@Override
				public void windowOpened(WindowEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void windowIconified(WindowEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void windowDeiconified(WindowEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void windowDeactivated(WindowEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
			public void windowClosing(WindowEvent arg0) {
					writeScore();	 
					
				}
				
				@Override
				public void windowClosed(WindowEvent e) {
					// TODO Auto-generated catch block
				}
				
				@Override
				public void windowActivated(WindowEvent arg0) {
					// TODO Auto-generated method stub
					
				}
			});
	}
	
	
	// creates main menu of game - oyun ana men�s�n� olu�turuyor
	public void addHomePage(){ 
		homePage = new JPanel();
		homePage.setBounds(0, 0, 1024, 768);
		homePage.setVisible(true);
		homePage.setOpaque(false);
		homePage.setLayout(null);
		JLabel irrBirdLogo = new JLabel(new ImageIcon(getClass().getResource("/resources/irregularBird.ico")));//irregularbird logo - irregularbird logosu
		irrBirdLogo.setBounds(200, 100, 625, 150);
		
		startLabel = new JLabel(new ImageIcon(getClass().getResource("/resources/baslaButonu.ico")));//start label - ba�lang�� label resmi
		startLabel.setBounds(240, 250, 285, 165);
		
		highScoreLabel = new JLabel(new ImageIcon(getClass().getResource("/resources/highScore.png")));//high score label - y�ksek skor label resmi
		highScoreLabel.setBounds(530, 260, 270, 150);
		
		settingsLabel = new JLabel(new ImageIcon(getClass().getResource("/resources/options.png")));//settings label - ayarlar label resmi
		settingsLabel.setBounds(250, 425, 270, 150);
		isSettingsPanelPressed=false;
		
		makersLabel = new JLabel(new ImageIcon(getClass().getResource("/resources/makers.png")));//makers label - yap�mc�lar label resmi
		makersLabel.setBounds(530, 425, 270, 150);
		
		
		//creates player name inputter - oyuncu ismi girilen k�sm� olu�turur.
		playerName = new JTextField("Enter Your Name Here!");
		playerName.setFont(new Font("Comic Sans MS", Font.BOLD, 15));
		playerName.setBackground(new Color(46, 204, 113));
		playerName.setForeground(Color.white);
		playerName.setBounds(430,210,180,50);
		
		textFieldPressed= false;
		
		playerName.addMouseListener(new MouseAdapter() {
			
			public void mousePressed(MouseEvent arg0) {
				
				if(!textFieldPressed){
					playerName.setText(null);
					textFieldPressed=true;
				}
			
			}
		});
		
		
		//adding components to panel - bile�enlerimizi panele ekliyoruz
		homePage.add(playerName);
		homePage.add(makersLabel);
		homePage.add(settingsLabel);
		homePage.add(highScoreLabel);
		homePage.add(startLabel);
		homePage.add(irrBirdLogo);
		contentPane.add(homePage);
		
		scoreLabel = new JLabel("0");
		scoreLabel.setFont(new Font("Stencil", Font.BOLD, 32));
		scoreLabel.setBounds(467, 20, 90, 50);
		scoreLabel.setVisible(false);
		contentPane.add(scoreLabel);
		
		
		//when you pressed to the start, the program organizes visibilities of game components, gives start to timer1 and checks boolean values of components.
		//starta bast���n�zda, program bile�enlerin g�r�n�rl�klerini d�zenliyor, timer1i ba�lat�yor ve boolean de�erleri d�zenliyor
		startLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				if(textFieldPressed){
					playerName.setVisible(false);
					
				}
				if(!textFieldPressed){
					
				}
				if(isMutePressed==true){
					soundOn=false;
				}else{
					soundOn=true;
				}
			
			
		
			homePage.setVisible(false);
			
			settingsPanel.setVisible(false);
			settingsPanelBackground.setVisible(false);
			isSettingsPanelPressed=false;
			
			bird.setVisible(true);
			scoreLabel.setVisible(true);
			for(int i=0;i<3;i++){
				upperConduit[i].setVisible(true);
				lowerConduit[i].setVisible(true);
				
				
			}
			timer1.start();
		
			}
			@Override
			public void mouseReleased(MouseEvent e) 
			{
			
			}
			
		});
		
		//when you pressed to the settings, the program organizes visibilities of game components and boolean values.When you pressed on it , shows a new panel.When you pressed on it second time,it hides the panel.
		//ayarlara bast���n�zda, program bile�enlerin g�r�n�rl�klerini ve boolean de�erleri d�zenliyor.Ayarlara bas�ld���nda ywni bir panel a��l�yor.ikinci kez bas�ld���nda ise yeni a��lan paneli gizliyor.
		settingsLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				if(isSettingsPanelPressed){
				settingsPanel.setVisible(false);
				settingsPanelBackground.setVisible(false);
				isSettingsPanelPressed=false;
				}else{
					settingsPanel.setVisible(true);
					settingsPanelBackground.setVisible(true);
					isSettingsPanelPressed=true;
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) 
			{
			
			}
		});
		
		//when you pressed to the makers, the program organizes visibilities of game components,shows makers panel, gives start to timer2 and same time start playing a sound file.
		//makers'a bast���n�zda, program bile�enlerin g�r�n�rl�klerini d�zenliyor,yap�mc�lar panelini g�steriyor,timer2 yi ba�lat�yor ve ayn� zamanda bir ses dosyas� �almaya ba�l�yor.
		makersLabel.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
		public void mousePressed(MouseEvent arg0) {
				
				
				startLabel.setVisible(false);
				makersLabel.setVisible(false);
				endMakersLabel.setVisible(true);
				playerName.setVisible(false);
				timer2.start();
				
					try {
			        	javax.sound.sampled.AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/resources/KeygenSoundFile.wav").getAbsoluteFile());
			        	makersClip = AudioSystem.getClip();
			        	makersClip.open(audioInputStream);
			        	makersClip.start();
			    	} catch(Exception ex) {
			    		System.out.println("Error with playing sound.");
			        ex.printStackTrace();
			    	}
				highScoreLabel.setVisible(false);
				scoresTextArea.setVisible(false);
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		//when you pressed to the high score, the program organizes visibilities of game components and boolean values and shows high scores saved on text file.On the second time you pressed on it, hides it. 
		//highscore'a bast���n�zda, program bile�enlerin g�r�n�rl�klerini d�zenliyor,txt dosyas�nda kay�tl� de�erleri g�steriyor,ikinci kez bas�nca da o paneli gizliyor.
		highScoreLabel.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			
			@Override
		public void mousePressed(MouseEvent arg0) {
				
				if(!isHighScorePressed){
					isHighScorePressed=true;
					scoresTextArea.setVisible(true);
					textToString="";
					scoresTextArea.setText(getTextLines());
				}else{
					scoresTextArea.setVisible(false);
					isHighScorePressed=false;
				}
				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
	}
	
	//adds settings panel to the contentPane.In content pane we have 4 different game speed option.
	//ayarlar men�s�n� contentPane'e ekliyoruz.bu panelde 4 tane farkl� oyun h�z� se�ene�imiz var
	public void addSettingsPanel(){
		 settingsPanel= new JPanel(new GridLayout(3, 1));
		settingsPanel.setOpaque(false);
		settingsPanel.setBounds(420,325,270,150);
		 settingsPanelBackground= new JPanel(new GridLayout(0,1));
		settingsPanelBackground.setOpaque(false);
		settingsPanelBackground.setBounds(390,310,270,150);
		
		JLabel gameSpeedBackgroundLabel = new JLabel(new ImageIcon(getClass().getResource("/resources/gameSpeedBack.png")));
		settingsPanelBackground.add(gameSpeedBackgroundLabel);
		//settingsPanel.setLayout(null);
		
		JRadioButton speed1 = new JRadioButton("Easy");
		speed1.setMnemonic(KeyEvent.VK_C);
		speed1.setOpaque(false);
		JRadioButton speed2 = new JRadioButton("Normal");
		speed2.setOpaque(false);
		speed2.setSelected(true);
		JRadioButton speed3 = new JRadioButton("Hard");
		speed3.setOpaque(false);
		JRadioButton speed4 = new JRadioButton("Expert");
		speed4.setOpaque(false);
		ButtonGroup speedButtonGroup = new ButtonGroup();
		
		speedButtonGroup.add(speed1);
		speedButtonGroup.add(speed2);
		speedButtonGroup.add(speed3);
		speedButtonGroup.add(speed4);
		
		
		
		//in this part when you click any game speed option,program sets birds and grounds sliding speed to specified values 
		//bu b�l�mde, herhangi bir oyun h�z� se�ene�ine t�klad���n�zda, program ku�un ve yerin kayma h�z�na belirli de�erler at�yor
		speed1.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				birdSpeedUp=2;
				birdSpeedDown=2;
				groundSlideSpeed=1;
			//	System.out.println(birdSpeedUp+" "+birdSpeedDown+" "+groundSlideSpeed);
			}
		});
		speed2.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				birdSpeedUp=3;
				birdSpeedDown=3;
				groundSlideSpeed=2;
		//		System.out.println(birdSpeedUp+" "+birdSpeedDown+" "+groundSlideSpeed);
			}
		});
		speed3.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				birdSpeedUp=6;
				birdSpeedDown=5;
				groundSlideSpeed=5;
		//		System.out.println(birdSpeedUp+" "+birdSpeedDown+" "+groundSlideSpeed);
			}
		});
		
		speed4.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				birdSpeedUp=10;
				birdSpeedDown=10;
				groundSlideSpeed=8;
		//		System.out.println(birdSpeedUp+" "+birdSpeedDown+" "+groundSlideSpeed);
				
			}
		});
		
			
		
		
		settingsPanel.add(speed1);
		settingsPanel.add(speed2);
		settingsPanel.add(speed3);
		settingsPanel.add(speed4);
		
		
		
		settingsPanel.setVisible(false);
		settingsPanelBackground.setVisible(false);
		contentPane.add(settingsPanel);
		contentPane.add(settingsPanelBackground);
		
	}
	
	
	//this method adds pause and resume label to contentPane.when you pressed on pause,program shows mute, shuts up the sounds and stops the timer1.when you pressed on resume program does just the opposite of pause.
	//bu metod duraklat ve devam et labellerini anapanele ekliyor.Pauseye bas�nca devam et butonunu gosteriyor,sesi susturuyor ve timer1i durduruyor.devam ede bas�ld���nda ise tam tersini yap�yor
	public void addPauseLabel(){
		final JLabel pause = new JLabel(new ImageIcon(getClass().getResource("/resources/pause.jpg")));
		pause.setBounds(940, 20, 50, 54);
		final JLabel resume = new JLabel(new ImageIcon(getClass().getResource("/resources/resume.jpg")));
		resume.setBounds(940, 20, 50, 54);
		
		pause.setVisible(true);
		resume.setVisible(false);
		
		pause.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
		public void mousePressed(MouseEvent arg0) {
				pause.setVisible(false);
				resume.setVisible(true);
				timer1.stop();
				soundOn=false;
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		resume.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
		public void mousePressed(MouseEvent e) {
				resume.setVisible(false);
				pause.setVisible(true);
				timer1.start();
				soundOn=true;
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		contentPane.add(pause);
		contentPane.add(resume);
	}
	
	//adds mute label
	//sesi sustur labelini ekliyor
	public void addMuteLabel(){
		final JLabel setSoundOn = new JLabel(new ImageIcon(getClass().getResource("/resources/soundOn.jpg")));
		setSoundOn.setBounds(875, 20, 50, 54);
		final JLabel mute = new JLabel(new ImageIcon(getClass().getResource("/resources/mute.jpg")));
		mute.setBounds(875, 20, 50, 54);
		
		setSoundOn.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
		public void mousePressed(MouseEvent arg0) {
				soundOn=false;
				mute.setVisible(true);
				setSoundOn.setVisible(false);
				isMutePressed=true;
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mute.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
		public void mousePressed(MouseEvent e) {
				soundOn=true;
				mute.setVisible(false);
				setSoundOn.setVisible(true);
				isMutePressed=false;
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
		contentPane.add(setSoundOn);
		contentPane.add(mute);
		
		setSoundOn.setVisible(true);
		mute.setVisible(false);
		
	}
	
	//adds makers label
	//yap�mc�lar labelini ekliyor
	public void addMakersLabel(){
		
		
		endMakersLabel = new JLabel(new ImageIcon(getClass().getResource("/resources/endMakers.png")));
		endMakersLabel.setBounds(0, 0, 1024, 3072);
		endMakersLabel.setVisible(false);
		contentPane.add(endMakersLabel);
		
		keyListener1 = new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent ke) {
				
				
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent ke) {
				if(ke.getKeyCode()==KeyEvent.VK_ESCAPE){
					endMakersLabel.setVisible(false);
					startLabel.setVisible(true);
					makersLabel.setVisible(true);
					timer2.stop();
					makersClip.stop();
					endMakersLabel.setLocation(0, 0);
					highScoreLabel.setVisible(true);
					
				}
				if((textFieldPressed&&!playerName.getText().equals(null))||!textFieldPressed){
					playerName.setVisible(true);
				}
				
			}
		};
		addKeyListener(keyListener1);
	}
	
	//adds game over panel
	//oyun bitimi panelini ekliyor
	public void addGameOver(){
		gameOverPanel = new JPanel();
		gameOverPanel.setBounds(0, 0, 1024, 768);
		gameOverPanel.setOpaque(false);
		gameOverPanel.setLayout(null);
		
		
		JLabel reStartLabel = new JLabel(new ImageIcon(getClass().getResource("/resources/baslaButonu.ico")));
		reStartLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
				if(soundOn){
					try {
			        	javax.sound.sampled.AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/resources/bidaha.wav").getAbsoluteFile());
			        	Clip clip = AudioSystem.getClip();
			        	clip.open(audioInputStream);
			        	clip.start();
			    	} catch(Exception ex) {
			    		System.out.println("Error with playing sound.");
			        ex.printStackTrace();
			    	}
				}
				
				if(isMutePressed==true){
					soundOn=false;
				}else{
					soundOn=true;
				}
				
				restartGame();
				
			}
		});
		reStartLabel.setBounds(250, 250, 285, 165);
		gameOverPanel.add(reStartLabel);
		
		showScore = new JLabel("0");
		showScore.setFont(new Font("Stencil", Font.BOLD, 32));
		showScore.setBounds(685, 300, 90, 50);
		showScore.setVisible(true);
		gameOverPanel.add(showScore);
		
		JLabel yourScoreLabel = new JLabel(new ImageIcon(getClass().getResource("/resources/yourScore.png")));
		gameOverPanel.add(yourScoreLabel);
		yourScoreLabel.setBounds(550, 250, 285, 165);
		
		JLabel highScoreLabel = new JLabel(new ImageIcon(getClass().getResource("/resources/highScore.png")));
		gameOverPanel.add(highScoreLabel);
		highScoreLabel.setBounds(550, 425, 285, 165);
		
		highScoreLabel.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			
			@Override
		public void mousePressed(MouseEvent arg0) {
				
				if(!isHighScorePressed){
					isHighScorePressed=true;
					textToString="";
					scoresTextArea.setText(getTextLines());
					scoresTextArea.setVisible(true);
					
				}else{
					scoresTextArea.setVisible(false);
					isHighScorePressed=false;
				}
				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		JLabel returnMainmenuLabel = new JLabel(new ImageIcon(getClass().getResource("/resources/returnMainMenu.png")));
		returnMainmenuLabel.setBounds(250, 425, 285, 165);
		gameOverPanel.add(returnMainmenuLabel);
		
		returnMainmenuLabel.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				bird.setVisible(false);
				scoreLabel.setVisible(false);
				for(int i=0;i<3;i++){
					upperConduit[i].setVisible(false);
					lowerConduit[i].setVisible(false);
				}
				gameOverPanel.setVisible(false);
				homePage.setVisible(true);
				
				scoreLabel.setText(String.valueOf("0"));
				showScore.setText(String.valueOf("0"));
				scoreCounter=0;
				
				
				for (int i = 0; i < 3; i++) 
				{
					upperConduit[i].setBounds(i*437 + 900, -400, 150, 600);
					lowerConduit[i].setBounds(i*437 + 900, 400, 150, 600);
				}
				
				bird.setBounds(250, 150, 100, 80);
			}
			
		});
		
		
		JLabel gameOverLabel = new JLabel(new ImageIcon(getClass().getResource("/resources/gameOver.ico")));
		gameOverLabel.setBounds(175, 75, 722, 160);
		gameOverPanel.add(gameOverLabel);
		
		
		
		gameOverPanel.setVisible(false);
		contentPane.add(gameOverPanel);
		
		
	}
	
	//checks collision between conduits,ground and bird
	//borular ve zeminin ku� ile kesi�imini kontrol ediyor
	protected boolean collisionCheck()
	{

		for (int i = 0; i < 3; i++)
		{
			upperConduitDown = upperConduit[i].getLocation().y + upperConduit[i].getHeight();
			upperConduitLeft = upperConduit[i].getLocation().x;
			upperConduitRight = upperConduit[i].getLocation().x + upperConduit[i].getWidth();
			
			lowerConduitUp = lowerConduit[i].getLocation().y;
			lowerConduitLeft = lowerConduit[i].getLocation().x;
			lowerConduitRight = lowerConduit[i].getLocation().x + lowerConduit[i].getWidth();
			
			birdUp = bird.getLocation().y;
			birdLeft = bird.getLocation().x;
			birdRight = bird.getLocation().x + bird.getWidth();
			birdDown = bird.getLocation().y + bird.getHeight();
			
			if(birdUp < upperConduitDown-5 && birdRight -10 > upperConduitLeft && birdLeft < upperConduitRight)
				return false;
			
			if(birdDown > lowerConduitUp+10 && birdRight -10> lowerConduitLeft && birdLeft < lowerConduitRight)
				return false;
			
			if(birdUp < -5)
				return false;
			
			if(birdDown > 600)
				return false;
			
		}
		
		
		return true;
		
	}
	
	//conditions about game over
	//oyun bitimi durumundaki kosullar
	public void gameOverStatus()
	{
		if(soundOn){
			try {
	        	javax.sound.sampled.AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/resources/aniyya.wav").getAbsoluteFile());
	        	Clip clip = AudioSystem.getClip();
	        	clip.open(audioInputStream);
	        	clip.start();
	    	} catch(Exception ex) {
	    		System.out.println("Error with playing sound.");
	        ex.printStackTrace();
	    	}
		}
		soundOn=false;
		
		
		if(scoreCounter>currentMaxScore){
			currentMaxScore=scoreCounter;
		}
//		organizeTextFile();
		
		gameOverPanel.setVisible(true);
//		reStartLabel.setVisible(true);
		timer1.stop();

			

	}
	
	//this method restarts the game
	//oyunu yeniden ba�latan metod
	public void restartGame()
	{
		scoreLabel.setText(String.valueOf("0"));
		showScore.setText(String.valueOf("0"));
		scoreCounter=0;
		//lbl_puan.setText("0");
		
		for (int i = 0; i < 3; i++) 
		{
			upperConduit[i].setBounds(i*437 + 900, -400, 150, 600);
			lowerConduit[i].setBounds(i*437 + 900, 400, 150, 600);
		}
		
		bird.setBounds(250, 150, 100, 80);
		
		timer1.start();	
		gameOverPanel.setVisible(false);
	//	reStartLabel.setVisible(false);
	}
	
	//adds background image
	//background labelini ekliyor
	public void addBackground(){
		JLabel background = new JLabel(new ImageIcon(getClass().getResource("/resources/arkaPlan.png")));
		background.setBounds(0, 0, 1024, 768);
		contentPane.add(background);
	}
	
	//adds bird's label
	//ku� labelini ekliyor
	public void addBird(){
		bird = new JLabel(new ImageIcon(getClass().getResource("/resources/maviKus.ico")));
		bird.setBounds(250, 150, 90, 64);
		contentPane.add(bird);
		bird.setVisible(false);
	}
	
	//adds conduit labels
	//boru labellerini ekliyor
	public void addConduit()
	{
		ImageIcon upCond = new ImageIcon(getClass().getResource("/resources/ustBoru.ico"));
		ImageIcon downCond = new ImageIcon(getClass().getResource("/resources/altBoru.ico"));
		
		upperConduit = new JLabel[3];
		lowerConduit = new JLabel[3];
		
		for (int i = 0; i < 3; i++) 
		{
			
			upperConduit[i] = new JLabel(upCond);
			upperConduit[i].setBounds(i*587 + 900, i*50-400, 150, 600);
			contentPane.add(upperConduit[i]);
			upperConduit[i].setVisible(false);
					
			lowerConduit[i] = new JLabel(downCond);
			lowerConduit[i].setBounds(i*587 + 900, i*50+400, 150, 600);
			contentPane.add(lowerConduit[i]);
			lowerConduit[i].setVisible(false);
			
		}
	}
	
	//adds the ground label
	//zemin labelini ekliyor 
	public void addGround(){
		ImageIcon zeminImg = new ImageIcon(getClass().getResource("/resources/zemin.png"));
		leftGround = new JLabel(zeminImg);
		leftGround.setBounds(0, 590, 2305, 205);
		contentPane.add(leftGround);
		leftGround.setVisible(true);
		
		rightGround = new JLabel(zeminImg);
		rightGround.setBounds(2305, 590, 2305, 205);
		contentPane.add(rightGround);
	}
	
	//this method for sliding conduits and ground
	//zemini ve borular� kayd�rmak i�in kulland���m�z metodw
	public void slideConduitsAndGround()
	{
		
		for (int i = 0; i < 3; i++)
		{
			upperConduit[i].setLocation(upperConduit[i].getLocation().x -groundSlideSpeed, upperConduit[i].getLocation().y);
			lowerConduit[i].setLocation(lowerConduit[i].getLocation().x -groundSlideSpeed, lowerConduit[i].getLocation().y);
		}
		leftGround.setLocation(leftGround.getLocation().x -groundSlideSpeed, leftGround.getLocation().y);
		rightGround.setLocation(rightGround.getLocation().x -groundSlideSpeed, rightGround.getLocation().y);
		
	}
	
	
	//this method for refreshing the conduits
	//boru yenilenmesi i�in kullan�l�yor
	public void refreshConduits(){
		Random ran = new Random();
		
		for (int i = 0; i < 3; i++) 
		{
			if(upperConduit[i].getLocation().x+150 < 0 || lowerConduit[i].getLocation().x+150 < 0)
			{
				
				int startPointOfUpperConduit = ran.nextInt(200);

				int destinationBetweenConduits = ran.nextInt(50);
				if(i==0){
					upperConduit[0].setLocation(upperConduit[2].getX()+437+150, (startPointOfUpperConduit - 435 - 100));
					lowerConduit[0].setLocation(lowerConduit[2].getX()+437+150, (upperConduit[i].getLocation().y + upperConduit[i].getHeight() + 165 + destinationBetweenConduits));
				}
				if(i==1){
					upperConduit[1].setLocation(upperConduit[0].getX()+437+150, (startPointOfUpperConduit - 435 - 100));
					lowerConduit[1].setLocation(lowerConduit[0].getX()+437+150, (upperConduit[i].getLocation().y + upperConduit[i].getHeight() + 165 + destinationBetweenConduits));
				}
				if(i==2){
					upperConduit[2].setLocation(upperConduit[1].getX()+437+150, (startPointOfUpperConduit - 435 - 100));
					lowerConduit[2].setLocation(lowerConduit[1].getX()+437+150, (upperConduit[i].getLocation().y + upperConduit[i].getHeight() + 165 + destinationBetweenConduits));
				}
				
			}
			
		}
	}
	
	//this method for refreshing the ground
	//zemin yenilenmesi i�in kullan�l�yor
	public void refreshGround(){
		if(leftGround.getX()+2305<=0){
			leftGround.setLocation(rightGround.getLocation().x+2305,590);
		}
		if(rightGround.getX()+2305<=0){
			rightGround.setLocation(leftGround.getLocation().x+2305,590);
		}
	}
	
	//counts the score by passing between two conduits
	//iki borunun aras�ndan ge�erek skoru art�r�yor
	public void scoring(){
		for (int i = 0; i < 3; i++){
			if(groundSlideSpeed==1){
				if(upperConduit[i].getLocation().x+75==bird.getLocation().x+45){
					scoreCounter++;
					scoreLabel.setText(String.valueOf(scoreCounter));
					showScore.setText(String.valueOf(scoreCounter));
				}
			}
			if(groundSlideSpeed==2){
				if(upperConduit[i].getLocation().x+75==bird.getLocation().x+45 || 
						upperConduit[i].getLocation().x+75==bird.getLocation().x+46){
					scoreCounter++;
					scoreLabel.setText(String.valueOf(scoreCounter));
					showScore.setText(String.valueOf(scoreCounter));
				}
			}
			if(groundSlideSpeed==5){
				if(upperConduit[i].getLocation().x+75==bird.getLocation().x+45 || 
						upperConduit[i].getLocation().x+75==bird.getLocation().x+46 ||
						upperConduit[i].getLocation().x+75==bird.getLocation().x+47 ||
						upperConduit[i].getLocation().x+75==bird.getLocation().x+48 ||
						upperConduit[i].getLocation().x+75==bird.getLocation().x+49  ){
					scoreCounter++;
					scoreLabel.setText(String.valueOf(scoreCounter));
					showScore.setText(String.valueOf(scoreCounter));
				}
			}
			if(groundSlideSpeed==8){
				if(upperConduit[i].getLocation().x+75==bird.getLocation().x+45 || 
						upperConduit[i].getLocation().x+75==bird.getLocation().x+46 ||
						upperConduit[i].getLocation().x+75==bird.getLocation().x+47 ||
						upperConduit[i].getLocation().x+75==bird.getLocation().x+48 ||
						upperConduit[i].getLocation().x+75==bird.getLocation().x+49 ||
						upperConduit[i].getLocation().x+75==bird.getLocation().x+50||
						upperConduit[i].getLocation().x+75==bird.getLocation().x+51||
						upperConduit[i].getLocation().x+75==bird.getLocation().x+52 ){
					scoreCounter++;
					scoreLabel.setText(String.valueOf(scoreCounter));
					showScore.setText(String.valueOf(scoreCounter));
				}
			}
		}
	}
	
	//writing score to a text file
	//skoru bir text dosyas�na yaz�yor
	public void writeScore(){
		
		try {
			highScoreText = new BufferedWriter(new FileWriter("src/resources/text.txt",true));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		if(textFieldPressed&&playerName.getText().equals("")||!textFieldPressed){
		//	System.out.println(playerName.getText().equals(""));
		//	System.out.println(textFieldPressed);
			
		}else{
			try {
				highScoreText.flush();
				highScoreText.write(currentMaxScore + " - "+playerName.getText()+"\n");
			} catch (IOException ee) {
				// TODO Auto-generated catch block
				ee.printStackTrace();
			}
			 try {
					highScoreText.close();
				} catch (IOException ee) {
					// TODO Auto-generated catch block
					ee.printStackTrace();
				}
		}
	
	}
	
	//creates a text area with saved scores
	//kaydedilen skorlarla bir text alan� olu�turuyor
	public void addHighScoreArea(){
		
		scoresTextArea= new JTextArea(getTextLines());
		scoresTextArea.setOpaque(true);
		scoresTextArea.setFont(new Font("Comic Sans MS", Font.BOLD, 15));
		scoresTextArea.setBackground(new Color(231,76,60));
		scoresTextArea.setForeground(Color.WHITE);
		scoresTextArea.setBounds(240, 250, 295, 340);
		add(scoresTextArea);
		scoresTextArea.setVisible(false);
		
	}
	
	//reads text lines from a file and converts them to a string
	//bir text dosyas�ndan yaz�lar� okuyor ve string hale �eviriyor
	public String getTextLines(){
	
		
		
		try {
			BufferedReader bfr1 = new BufferedReader(new FileReader("src/resources/text.txt"));
			while (bfr1.ready()){
				textToString=textToString+bfr1.readLine()+"\n";
			}
		} catch (FileNotFoundException e1) {
			System.out.println("File Not Found ):  -  Dosya bulunamad� ):");
			e1.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error with reading text file - Dosya okunurken hata olu�tu!");
			e.printStackTrace();
		};
		
		
		return textToString;
	}

}
