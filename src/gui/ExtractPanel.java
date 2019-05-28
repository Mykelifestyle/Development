package gui;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.*;

import engine.StegoImage;

@SuppressWarnings("serial")
public class ExtractPanel extends JPanel {

	private StegoImage stegoImg;
	private JPasswordField seed;
	
	public ExtractPanel() {
		super(new BorderLayout(0, 10));
		
		final JTextField imgName = new JTextField(15);
		imgName.setEnabled(false);
						
		seed = new JPasswordField(15);
		
		JButton browseImg = new JButton("Browse");
		browseImg.setBackground(Color.CYAN);
		browseImg.setFocusable(false);
		browseImg.addActionListener(arg0 -> {
			File image = ((StegoHide)getTopLevelAncestor()).selectImage();
			if (image == null)
				return;

			try {
				stegoImg = new StegoImage(ImageIO.read(image), StegoImage.EXTRACT_MODE);
				imgName.setText(image.getName());
			} catch (Exception e) {

			}
		});
	
		JButton extract = new JButton("Extract File");
		extract.setBackground(Color.GREEN);
		extract.setFocusable(false);
		extract.addActionListener(arg0 -> extractFile());
		
		JLabel imageLabel = new JLabel("Stego Image");
		imageLabel.setPreferredSize(new JLabel("Payload Capacity").getPreferredSize());
		
		JPanel p1 = new JPanel(new GridLayout(5, 1, 0, 5));
		p1.setBackground(Color.lightGray);
		p1.add(imageLabel);
		p1.add(new JLabel("StegoKey"));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
		
		
		JPanel p2 = new JPanel(new GridLayout(5, 1, 0, 5));
		p2.setBackground(Color.lightGray);
		p2.add(imgName);
		p2.add(seed);
		
		JPanel p3 = new JPanel(new GridLayout(5, 1, 0, 5));
		p3.setBackground(Color.lightGray);
		p3.add(browseImg);
		p3.add(Box.createGlue());
		
		JPanel p4 = new JPanel(new BorderLayout(10, 0));
		p4.add(p1, BorderLayout.WEST);
		p4.add(p2);
		p4.add(p3, BorderLayout.EAST);
		
		add(p4);
		add(extract, BorderLayout.SOUTH);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}
	
	private void extractFile() {
		if (stegoImg == null) {
			((StegoHide)getTopLevelAncestor()).showErrorMessage("Please select an image!");
			return;
		}
		
		int seedValue;
		try {
			seedValue = Integer.parseInt(seed.getText());
		} catch(NumberFormatException ex) {
			((StegoHide)getTopLevelAncestor()).showErrorMessage("Please enter a valid code in the seed field!");
			return;
		}
		
		try {
			byte[] data = stegoImg.extract(seedValue);
			String format = new String(Arrays.copyOfRange(data, 0, 5)).trim();
			
			JFileChooser jfc = new JFileChooser();
			jfc.setSelectedFile(new File("SecretExtracted." + format));
			((StegoHide)getTopLevelAncestor()).showInformationMessage("The Extraction process is successful!");
			int result = jfc.showSaveDialog(this);
			if (result != JFileChooser.APPROVE_OPTION)
				return;
			
			FileOutputStream fos = new FileOutputStream(jfc.getSelectedFile());
	        fos.write(Arrays.copyOfRange(data, 5, data.length));
	        fos.flush();
	        fos.close();

		} catch(Exception ex) {
			((StegoHide)getTopLevelAncestor()).showErrorMessage("Could not extract the hidden file from the image!");
		}
	}

}
