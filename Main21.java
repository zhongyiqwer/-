
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.filechooser.FileSystemView;


//微信一笔画完辅助
//关键找路径，图的截取，图的转换，模拟点击
public class Main21 {

	static int point = 0;
	static int x = 0;
	static int y = 0;
	static int n = 6;
	static int m = 6;
	static int lastArr = 1;
	static int miaoX = 0;
	static int miaoY = 0;
	static int interval = 0;
	static Stack<Integer> arr = new Stack<>();
	private static String deskTopPath = "";
	private static int startCount=0;
	public static void main(String[] args) {
		
		FileSystemView fsv = FileSystemView.getFileSystemView();
		File com=fsv.getHomeDirectory();    //这便是读取桌面路径的方法了
		System.out.println(com.getPath());
		deskTopPath = com.getPath();
		
		int [][] input = getImage();
		if (input!=null) {
			//n为行 m为列
			System.out.println("hang="+n+"  lie="+m);
			System.out.println(x+"  "+y);
			int xx = miaoX + interval*y+interval/2;
			int yy = miaoY + interval*x+interval/2;
			System.out.println("起始坐标："+xx+"  "+yy);
			Stack<Integer> stack = new Stack<>();
			stack.push(x*m+y);
			find(input,stack);
		}else {
			//初始状态不正确时，重置
			getAdbShell();
		}
	}

	private static void getAdbShell(Stack<Integer> outStack) {
		String batPath = deskTopPath+"\\weixin\\test.bat";
		Iterator<Integer> iterator = outStack.iterator();
		iterator.next();
		FileOutputStream fs=null;
		PrintStream pStream = null;
		try {			
			fs =  new FileOutputStream(batPath);
			pStream = new PrintStream(fs);
			while (iterator.hasNext()) {
				int temp = iterator.next();
				int startM = temp%m;
				int startN = temp/m;
				int pointX = miaoX+startM*interval+interval/2;
				int pointY = miaoY+startN*interval+interval/2;
				pStream.println("adb shell input tap "+pointX+" "+pointY);	
				//System.out.println("坐标："+pointX+"  "+pointY);
			}
			//暂停5秒
			pStream.println("@ping 127.0.0.1 -n 4 >nul");
			pStream.println("adb shell input tap "+550+" "+1100);
			pStream.println("@ping 127.0.0.1 -n 2 >nul");
			pStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (pStream!=null) {
				pStream.close();
			}
			if (fs!=null) {
				try {
					fs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void getAdbShell() {
		String batPath = deskTopPath+"\\weixin\\test.bat";
		FileOutputStream fs=null;
		PrintStream pStream = null;
		try {			
			fs =  new FileOutputStream(batPath);
			pStream = new PrintStream(fs);
			pStream.println("@ping 127.0.0.1 -n 2 >nul");
			pStream.println("adb shell input tap "+210+" "+1770);
			pStream.println("@ping 127.0.0.1 -n 2 >nul");
			pStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (pStream!=null) {
				pStream.close();
			}
			if (fs!=null) {
				try {
					fs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	private static int[][] getImage() {
		File file = new File(deskTopPath+"\\weixin\\picname.png");
		String outPath = deskTopPath+"\\weixin\\picname1.png";
		String outPath2 = deskTopPath+"\\weixin\\picname2.png";
		BufferedImage image;
		int input[][] = null;
		try {
			//初步剪裁
			ImageInputStream iss = ImageIO.createImageInputStream(new FileInputStream(file));
			Iterator it = ImageIO.getImageReadersByFormatName("png");
			ImageReader imageReader = (ImageReader)it.next();
			imageReader.setInput(iss);
			ImageReadParam param = imageReader.getDefaultReadParam();
			param.setSourceRegion(new Rectangle(0, 360, 1080, 1340));
			BufferedImage bImage = imageReader.read(0, param);
			ImageIO.write(bImage, "png", new File(outPath));
			
			//计算边缘裁剪时上 下 左的大小
			int topOff = getOffSize(bImage,1);
			int height1 = getOffSize(bImage,2);
			int leftOff = getOffSize(bImage,3);
			
			int startX = leftOff;
			int startY = 360+topOff;
			int widht = 1080-leftOff*2;
			int height = height1-topOff;
			
			miaoX = startX;
			miaoY = startY;
			System.out.println(miaoX+"  "+miaoY);
			//边缘裁剪
			param.setSourceRegion(new Rectangle(startX, startY, widht,height));
			BufferedImage bImage2 = imageReader.read(0, param);
			ImageIO.write(bImage2, "png", new File(outPath2));
			System.out.println("leftOff = "+leftOff);
			System.out.println("widht="+bImage2.getWidth()+" height="+bImage2.getHeight());
			int start =leftOff;
			
			//根据左边裁剪大小来确定小方格大小，从而计算二维数组的行和列大小
			if (start == 130) {
				//m=4
				interval=200;
				m=bImage2.getWidth()/200;
				n=bImage2.getHeight()/200;
			}else if (start==235) {
				//m=3
				interval=200;
				m=bImage2.getWidth()/200;
				n=bImage2.getHeight()/200;
			}else if (start==105) {
				//m=5
				interval=170;
				m=bImage2.getWidth()/170;
				n=bImage2.getHeight()/170;
			}else if (start==195) {
				//m=4
				interval=170;
				m=bImage2.getWidth()/170;
				n=bImage2.getHeight()/170;
			}else if (start==75) {
				//m=6
				interval=150;
				m=bImage2.getWidth()/150;
				n=bImage2.getHeight()/150;
			}else if (start==285) {
				//m=3
				interval=170;
				m=bImage2.getWidth()/170;
				n=bImage2.getHeight()/170;
			}else if (start==95) {
				interval=126;
				m=bImage2.getWidth()/126;
				n=bImage2.getHeight()/126;
			}else if (start==160) {
				interval=126;
				m=bImage2.getWidth()/126;
				n=bImage2.getHeight()/126;
			}
			
			//更具左边裁剪大小 leftOff=start来得到二维数组
			input = new int[n][m];
			int rgb1=0;
			for(int i=0;i<n;i++) {
				for(int j=0;j<m;j++) {
					if (start==285 || start==195 || start==105) {
						rgb1 = bImage2.getRGB(85+j*170, 85+i*170);
					}else if (start==130 || start==235) {
						rgb1 = bImage2.getRGB(100+j*200, 100+i*200);
					}else if (start==75) {
						rgb1 = bImage2.getRGB(75+j*150, 75+i*150);
					}else if (start==95 || start==160) {
						rgb1 = bImage2.getRGB(62+j*126, 62+i*126);
					}
					
					//System.out.print(rgb1+" ");
					//-3026479灰色  -394759白色
					if (rgb1==(-3026479)) {
						input[i][j] = 1;
						point++;
					}else if (rgb1 == -394759) {
						input[i][j] = 0;
					}else {
						//起始坐标
						x=i;
						y=j;
						input[i][j] = 0;
						startCount++;
						if(startCount>1) {
							return null;
						}
						System.out.println("找到起始点坐标:"+startCount);
					}
				}
				//System.out.println();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return input;
	}

	//对初步图片进行边缘裁剪
	private static int getOffSize(BufferedImage bImage,int order) {
		int start=0;
		//裁剪上边
		if (order==1) {
			for(int y=5;y<1340;y+=5) {
				for(int j=80;j<1080;j+=30) {
					if (bImage.getRGB(j, y)!= -394759) {
						//System.out.println(bImage.getRGB(j, y));
						if (bImage.getRGB(j, y) == -3026479) {
							start = y;
							break;
						}else {
							//System.out.println("走了加30");
							start = y+30;
						}
						//System.out.println("start = "+start);
					}
				}
				if (start!=0) {
					break;
				}
			}
			//裁剪下边
		}else if (order==2) {
			for(int y=1335;y>0;y-=5) {
				for(int j=80;j<1080;j+=30) {
					if (bImage.getRGB(j,y)!= -394759) {
						start = y;
						break;
					}
					//System.out.println(bImage.getRGB(j, y)+" "+"xia = "+y);
				}
				if (start!=0) {
					break;
				}

			}
			//裁剪左边
		}else if (order==3) {
			for(int x=5;x<1080;x+=5) {
				for(int j=80;j<1080;j+=30) {
					if (bImage.getRGB(x, j)!= -394759) {
						start = x;
						break;
					}
				}
				if (start!=0) {
					break;
				}
			}
		}
		return start;
	}

	private static Stack<Integer> find(int[][] input, Stack<Integer> stack) {
		//System.out.println("point="+point);
		//System.out.println("x="+x+" y="+y);
		if (lastArr==0) {
			return null;
		}else if (point==0) {
			//当所有灰色小方格走完就可以得到路径了
			System.out.println("结束 "+stack.size());
			lastArr = 0;
			System.out.println(stack.toString());
			getAdbShell(stack);
			return stack;
		}
		
		
		if (checkUp(input, x, y)) {
			//System.out.println("上");
			arr.push(1);
			x-=1;
			point--;
			input[x][y] = 0;
			stack.push(x*m+y);
			find(input, stack);
		}
		if (checkLeft(input, x, y)) {
			//System.out.println("左");
			arr.push(2);
			y-=1;
			point--;
			input[x][y] = 0;
			stack.push(x*m+y);
			find(input, stack);
		}
		if (checkDown(input, x, y)) {
			//System.out.println("下");
			arr.push(3);
			x+=1;
			point--;
			input[x][y] = 0;
			stack.push(x*m+y);
			find(input, stack);
		}
		if (checkRight(input, x, y)) {
			//System.out.println("右");
			arr.push(4);
			y+=1;
			point--;
			input[x][y] = 0;
			stack.push(x*m+y);
			find(input, stack);
		}
		point++;
		input[x][y] = 1;
		stack.pop();
		switch (arr.pop()) {
		case 1:
			x++;
			break;
		case 2:
			y++;
			break;
		case 3:
			x--;
			break;
		case 4:
			y--;
			break;
		}
		
		return stack;
	}

	//检查向上的合法性
	private static boolean checkUp(int[][] input, int x, int y) {
		if (x-1>=0 && input[x-1][y]==1) {
			//上
			return true;
		}
		return false;
	}
	
	private static boolean checkLeft(int[][] input, int x, int y) {
		if (y-1>=0 && input[x][y-1]==1) {
			return true;
		}
		return false;
	}
	
	private static boolean checkDown(int[][] input, int x, int y) {
		if (x+1<n && input[x+1][y]==1) {
			return true;
		}
		return false;
	}
	
	private static boolean checkRight(int[][] input, int x, int y) {
		if (y+1<m && input[x][y+1]==1) {
			return true;
		}
		return false;
	}

	private static int check(int[][] input, int n, int m, int x, int y) {
		if (x-1>=0 && input[x-1][y]==1) {
			//上
			return 1;
		}else if (y-1>=0 && input[x][y-1]==1) {
			//左
			return 2;
		}else if (x+1<n && input[x+1][y]==1) {
			//下
			return 3;
		}else if (y+1<m && input[x][y+1]==1) {
			//右
			return 4;
		}
		return 0;
	}

}
