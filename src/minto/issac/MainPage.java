package minto.issac;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JList;

import net.sf.image4j.codec.bmp.BMPDecoder;
import net.sf.image4j.codec.ico.ICOEncoder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.omg.CORBA.NameValuePair;

import java.awt.List;


public class MainPage extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField txtFolder;
	private JButton btnGetInfo;
	private JList list;
	private List list_1;
	int processed=0;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					 //String pathToImageSortBy = "/resources/splash.png";
					    //ImageIcon SortByIcon = new ImageIcon(getClass().getClassLoader().getResource(pathToImageSortBy));
					//SplashWindow.splash(SortByIcon.getImage());
					MainPage frame = new MainPage();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	ArrayList<NameValuePair > li;HashMap<String, String> nvp;
	/**
	 * Create the frame.
	 */
	JDialog dlg;
	 JProgressBar dpb;JLabel jl;
	public MainPage() {
		nvp=new HashMap<String,String>();
		txtFolder = new JTextField();
		txtFolder.setText("folder");
		getContentPane().add(txtFolder, BorderLayout.NORTH);
		txtFolder.setColumns(10);
		li=new ArrayList<NameValuePair>();
		JButton btnSelectFolder = new JButton("Select folder");
		getContentPane().add(btnSelectFolder, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		list = new JList(); //data has type Object[]
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);
		//...
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(250, 80));
		setContentPane(contentPane);
		
		textField = new JTextField();
		contentPane.add(textField, BorderLayout.NORTH);
		textField.setColumns(10);
		
		JButton btnChoseFolder = new JButton("Chose Folder");
		btnChoseFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				 chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		        Component parent = null;
		        int returnVal = chooser.showOpenDialog(parent);
		        DefaultListModel listModel = new DefaultListModel();
		        
		        if(returnVal == JFileChooser.APPROVE_OPTION) {
		               System.out.println("You chose to open this file: " + chooser.getSelectedFile().getAbsolutePath());
		               textField.setText(chooser.getSelectedFile().getAbsolutePath());
		               File dir = new File(chooser.getSelectedFile().getAbsolutePath());  
		               
		               File[] subDirs = dir.listFiles(new FileFilter() {  
		                   public boolean accept(File pathname) {  
		                       return pathname.isDirectory();  
		                   }  
		               });  
		                  
		               for (File subDir : subDirs) {  
		                   System.out.println(subDir.getName()); 
		                   
		                   nvp.put(subDir.getName(),subDir.getAbsolutePath().toString());
		                   list_1.add(processMovieName(subDir.getName()));
		                  // listModel.addElement(subDir.getName());
		               } 
		               
		            }               
		            
			}

			
		});
		contentPane.add(btnChoseFolder, BorderLayout.WEST);
		
		btnGetInfo = new JButton("Get Info");
		btnGetInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					 Iterator it=nvp.entrySet().iterator();
//					 dlg  = new JDialog(MainPage.this, "Downloading Movie Info", true);
//					dpb = new JProgressBar(0, 500);
//					    dlg.add(BorderLayout.CENTER, dpb);
//					    jl=new JLabel("Downloading...");
//				    dlg.add(BorderLayout.NORTH, jl);
//					    dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
//					    dlg.setSize(300, 75);
//					    dlg.setLocationRelativeTo(MainPage.this);
//					    //dlg.setVisible(true);
//					    dlg.setVisible(true);
					while(it.hasNext())
					{
						Map.Entry pairs = (Map.Entry)it.next();
						String name=(String) pairs.getKey();
						System.out.println(name);
						System.out.println(pairs.getValue().toString());
						String moviename=processMovieName(name);
						//jl.setText("Processing:"+moviename);
					   //dpb.setValue(processed);
						//jl.setText("Procesing "+moviename);
						//dpb.setValue(dpb.getValue()+10);
						MainPage.this.setTitle("Processing : "+moviename);
						String url = "http://www.omdbapi.com/?t="+URLEncoder.encode(moviename);
						jsonParse(sendGet(url,pairs.getValue().toString()),pairs.getValue().toString(),pairs.getKey().toString());
						System.out.println("Current item "+processed+" "+moviename);
						
					}
					MainPage.this.setTitle("Completed.");
					System.out.println("Finished");
					JOptionPane.showMessageDialog(MainPage.this, "Completed");
					dlg.dispose();
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		contentPane.add(btnGetInfo, BorderLayout.EAST);
		
		list_1 = new List();
		contentPane.add(list_1, BorderLayout.CENTER);
	}
	private String sendGet(String url,String path) throws Exception {
		 
		@SuppressWarnings("deprecation")
		
 
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		// optional default is GET
		con.setRequestMethod("GET");
 
		//add request header
		//con.setRequestProperty("User-Agent", USER_AGENT);
 
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
 
		//print result
		System.out.println(response.toString());
		return response.toString();
 
	}
	
	private void jsonParse(String string,String path, String moviename) throws ParseException {
		JSONParser jp=new JSONParser();
		System.out.println("Movie name @@@@@@@@"+string);
		try {

			JSONObject prime=(JSONObject) jp.parse(string);
			String title= prime.get("Title").toString();
			String year = prime.get("Year").toString();
			String img_url="nil";
					img_url=prime.get("Poster").toString();
			String imdbid=prime.get("imdbID").toString();
			saveImage(img_url,path+"\\folder.jpg",title,year,imdbid,path);
			HashMap<String, String> info=new HashMap<String,String>();
			info.put("name", title);
			info.put("year", year);
			info.put("lang", prime.get("Language").toString());
			info.put("prate", prime.get("Rated").toString());
			info.put("genre", prime.get("Genre").toString());
			info.put("dir", prime.get("Director").toString());
			info.put("writer", prime.get("Writer").toString());
			info.put("actors", prime.get("Actors").toString());
			info.put("plot", prime.get("Plot").toString());
			info.put("awards", prime.get("Awards").toString());
			info.put("rating", prime.get("imdbRating").toString());
			info.put("rel", prime.get("Released").toString());
			String HTML=new InfoGen().InfoStringGen(info);
			PrintWriter pr=new PrintWriter(path+"\\Info.html");
			pr.println(HTML);
			pr.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//System.out.println("Cannot parse "+string);
			//e.printStackTrace();
			JSONObject prime=(JSONObject) jp.parse(string);
			String Error=(String) prime.get("Error");
			try{
			if(Error.contains("Movie not found!"))
			{
				System.out.println("Movie not found trying Google Image search....");
				try {
					saveImage("nil", path+"\\folder.jpg", moviename, "", "",path);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					System.out.println("Njan tholvi sammathichirikkunnu!!!");
				}
			}
			}
			catch(NullPointerException nullpException)
			{
				System.out.println("^^^^^^^^^^^^^^^^^^^"+string);
			}
			//e.printStackTrace();
		}
		
	}
	public  void saveImage(String imageUrl, String destinationFile, String title, String year, String imdbid,String path) throws IOException {
//		PrintWriter pr=new PrintWriter(new File(path+"\\desktop.ini"));
//		pr.println("[.ShellClassInfo]");
//		pr.println("InfoTip=");
//		
//		pr.println("IconResource=folder.ico,0");
//		pr.println("Logo=folder.ico");
//		pr.close();
		if(imageUrl.equals("nil")||imageUrl.equals("N/A"))
		{
			String googleSearchURL="https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="+URLEncoder.encode(imdbid+" "+title+" "+year+" poster");
			try {
				System.out.println("###############"+googleSearchURL);
				String response = sendGet(googleSearchURL, destinationFile);
				System.out.println("###############"+googleSearchURL);
				imageUrl=googleImageParser(response);
				System.out.println(imageUrl);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+imageUrl);
		URL url = new URL(imageUrl);
		try{
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(destinationFile);

		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}

		is.close();
		os.close();
		urlStack.removeAllElements();
		processed++;
		
		}catch(FileNotFoundException e)
		{
			try{
			saveImage(urlStack.pop(),destinationFile,  title, year, imdbid,path);
			}
			catch(EmptyStackException emptyStackException)
			{
				System.out.println("Nothing to pop");
			}
		}
		try{
		Image image = ImageIO.read(new File(path+"\\folder.jpg"));
		BufferedImage images=toBufferedImage(image);
		
		//ICOEncoder.write(images, new File(path+"\\folder.ico"));
		
		}
		catch(Exception e)
		{
			
		}
	}
	Stack<String> urlStack=new Stack<String>();
	private String googleImageParser(String response) {
		// TODO Auto-generated method stub
		JSONParser paeser=new JSONParser();
		try {
			JSONObject root=(JSONObject) paeser.parse(response);
			JSONObject jsonObject=(JSONObject)root.get("responseData");
			JSONArray results=(JSONArray) jsonObject.get("results");
			for(int i=0;i<results.size();i++)
			{
				String url=(String) ((JSONObject)results.get(i)).get("url");
				urlStack.push(url);
			}
			return urlStack.pop();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Ennekkond ith parse cheyyan vayya @@@@@@@@@@@\n"+response);
			return urlStack.pop();
		}
		catch (NullPointerException e) {
			// TODO: handle exception
			System.out.println("Chilappo reqeust exceed aayikkanum,kuzhappam illa");
			return urlStack.pop();
		}
		
	}
	private static String processMovieName(String name) 
	{
					
	// TODO Auto-generated method stub
				String copyOfName=name;	
	Pattern regex = Pattern.compile("\\d{4}");
	Matcher m = regex.matcher(name);
	//return 
	try{
	
	
	name=name.replaceAll("[^\\w\\s-]", " ");
	name=name.replaceAll("DVDRip", " ");
	name=name.replaceAll("XViD", " ");
	name=name.replaceAll("DVD", " ");
	name=name.replaceAll("dvd", " ");
	name=name.replaceAll("BRRip", " ");
	name=name.replaceAll("XviD", " ");
	name=name.replaceAll("rip", " ");
	name=name.replaceAll("_", " ");
	name=name.replaceAll("ENG", " ");
	name=name.replaceAll("FR", " ");
	name=name.split("\\d{4}")[0];
	name=name.split("-")[0];
	
	String year=m.group(0);
	if(copyOfName.substring(0,3).equals(year))
		return year;
	System.out.println("Year :"+year);
	
	}catch(Exception e)
	{
		System.out.println("No year found");
	}
	return name;
	}
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
}
