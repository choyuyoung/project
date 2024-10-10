
package pj;

import java.util.*; 
import java.util.List; 
import java.awt.*; 
import java.awt.event.KeyAdapter; 
import java.awt.event.KeyEvent; 
  
import javax.swing.*; 
  
  
  
public class Main { 
    public static void main(String[] args) { 
        Game game=new Game(); 
    } 
} 
  
class Game extends JFrame{ 
     static int WIDTH=GamePanel.Block.SIZE*GamePanel.NUM_OF_BLOCKS_HOR+30; 
     static int HEIGHT=GamePanel.Block.SIZE*GamePanel.NUM_OF_BLOCKS_VER+50; 
  
    GamePanel gamePanel; 
     
    public Game() { 
        setVisible(true); 
        setLayout(new BorderLayout()); 
        getContentPane().setBackground(Color.BLACK); 
         
         
        gamePanel=new GamePanel(); 
         
        add(gamePanel, BorderLayout.CENTER); 
        gamePanel.requestFocus(); 
        setSize(WIDTH,HEIGHT); 
         
    } 
  
    public     enum Dir{RIGHT, LEFT, DOWN}; 
} 
  
     
     
  
class GamePanel extends JPanel{ 
    final static int NUM_OF_BLOCKS_VER=16; 
    final static int NUM_OF_BLOCKS_HOR=12; 
     
     
    final static int DELAY=400; 
    final static int SUB_DELAY=200; 
     
    static int [][][] SHAPE_COORD= { 
            { { 0, 0 },  { 0, -1 },   { -1, 0 },  { -1, 1 } }, 
            { { 0, 0 },  { 0, -1 },   { 1, 0 },   { 1, 1 } }, 
            { { 0, 0 },  { 0, -1 },   { 0, 2 },   { 0, 1 } }, 
            { { 0, 0 },  { -1, 0 },   { 1, 0 },   { 0, 1 } }, 
            { { 0, 0 }, { 0, -1 },  { -1,-1 },   { 0, 1 } }, 
            { { 0, 0 },  { 0, -1 },  { 1,-1 },   { 0, 1 } }, 
            { { 0, 0 },   { 1, 0 },   { 0, -1 },   { 1, -1 } }    //[6]은 rotate() X 
}; 
     
    Shape shape; 
    VirtualShape virtualShape; 
    List<Block> blocks; 
    BlockDownThread blockDownThread; 
    boolean isAlive=true; 
     
     
    int score; 
    JPanel infoPanel; 
    JLabel scoreLabel,infoLabel, msg; 
    //가로8, 세로8 
    public GamePanel() { 
        //setSize(500,700); 
        setPreferredSize(new Dimension(Game.WIDTH,GamePanel.HEIGHT)); 
        setVisible(true); 
        setBackground(Color.LIGHT_GRAY); 
        setLayout(new BorderLayout()); 
        infoPanel=new JPanel(); 
        infoPanel.setLayout(new BorderLayout()); 
        infoPanel.setOpaque(false); 
        scoreLabel=new JLabel("SCORE : "+score); 
        scoreLabel.setForeground(Color.LIGHT_GRAY); 
        scoreLabel.setFont(new Font(null,Font.BOLD, 15)); 
         
        infoLabel=new JLabel("<html>How To Play<br>W : Rotate<br>S : Go down<br>A,S : Go Right or Left<br>SPACE : Drop<br>ENTER : New Game<html>"); 
        infoLabel.setForeground(Color.LIGHT_GRAY); 
        infoLabel.setFont(new Font(null,Font.BOLD, 12)); 
         
        infoPanel.add(infoLabel, BorderLayout.EAST); 
        infoPanel.add(scoreLabel, BorderLayout.CENTER); 
         
        add(infoPanel, BorderLayout.NORTH); 
        revalidate(); 
         
        msg=new JLabel("GAME OVER__"); 
        msg.setFont(new Font(null,Font.BOLD, 21)); 
        msg.setForeground(Color.RED.darker()); 
         
        blocks=new ArrayList<Block>(); 
        genShape(); 
        blockDownThread=new BlockDownThread(); 
        blockDownThread.start(); 
        requestFocus(); 
        setFocusable(true); 
        addKeyListener(new KeyListener()); 
        setBackground(Color.BLACK); 
         
    } 
     
    class BlockDownThread extends Thread{ 
        public void run() { 
            while(isAlive) {    
                while(!shape.isFallen()) {    
                    try { 
                        Thread.sleep(DELAY); 
                    } catch (InterruptedException e) { 
                        // TODO Auto-generated catch block 
                            e.printStackTrace(); 
                    } 
                     
                         
                        if(!shape.isFallen()) { 
                            shape.Move(0,1);     
                            repaint(); 
                        } 
                         
                       
                         
                        if(shape.isFallen()) { 
                            try { 
                                Thread.sleep(SUB_DELAY); 
                            } catch (InterruptedException e) { 
                                // TODO Auto-generated catch block 
                                    e.printStackTrace(); 
                            } 
                            repaint();                             
                        } 
                    } 
                
                try { 
                    
                    sleep(10); 
                } catch (InterruptedException e) { 
                    // TODO Auto-generated catch block 
                    e.printStackTrace(); 
                } 
                 
                    for(Block block:shape.nowBlocks) { 
                        blocks.add(block); 
                    } 
                    if(isGameOver()) { 
                        isAlive=false; 
                        add(msg,BorderLayout.CENTER); 
                        break; 
                    } 
                int [] linesToRemove=shape.getEffectiveYs(); 
                 
                     
                for(int line:linesToRemove) { 
                    if(isLineFull(line)) { 
                        removeLine(line); 
                    } 
                } 
                 
                 
                genShape();     
                repaint(); 
            } 
                 
                 
            repaint(); 
            revalidate(); 
                 
        } 
         
    } 
     
    class KeyListener extends KeyAdapter{ 
        public void keyPressed(KeyEvent e) { 
             
            int key=e.getKeyCode(); 
             
            switch(key) { 
            case KeyEvent.VK_W:{ 
                shape.tryRotate(); 
            }     break; 
            case KeyEvent.VK_S:{ 
                if(shape.canMove(0,1)) { 
                    shape.Move(0,1); 
                    virtualShape.setLocation(shape.nowBlocks); 
                } 
            }    break; 
            case KeyEvent.VK_A:{ 
                if(shape.canMove(-1,0)) { 
                    shape.Move(-1,0); 
                    virtualShape.setLocation(shape.nowBlocks); 
                } 
            }    break; 
            case KeyEvent.VK_D:{ 
                if(shape.canMove(1,0)) { 
                    shape.Move(1,0); 
                    virtualShape.setLocation(shape.nowBlocks); 
                } 
            }    break; 
            case KeyEvent.VK_SPACE:{ 
                shape.drop(); 
                break; 
            } 
            case KeyEvent.VK_ENTER:{ 
                newGame(); 
            } 
            } 
  
            repaint(); 
            } 
        } 
     
     
    class Block{    
        final static int SIZE=30; 
        int x, y; 
        int type; 
         
        public Block(int type, int x, int y) { 
            this.type=type; 
            this.x=x; 
            this.y=y; 
        } 
        public Block(int type) { 
            this.type=type; 
            x=1; 
            y=1; 
        } 
        public Block(int x, int y) { 
            this.x=x; 
            this.y=y; 
             
           
        } 
         
        
        public void tryMove(int deltax, int deltay) { 
             
             
           
            if(deltay!=0) { 
                if(!isFallen()) { 
                    y+=deltay; 
                } 
                 
                else { 
                    System.out.println("이미 떨어졌음"); 
                } 
            } 
             
             
            //블럭이 희망하는 위치에 없으면 
                else{ 
                    if(!isBlockAt(x+deltax,y+deltay)) { 
  
                        x+=deltax; 
                        y+=deltay; 
                    } 
                    else System.out.println("tryMove 실패"); 
                } 
                 
                 
            } 
             
            public boolean isFallen() { 
                if(isBlockAt(x,y+1)||y==NUM_OF_BLOCKS_VER) { 
                    return true; 
                } 
                else return false; 
            } 
             
            public void draw(Graphics g) { 
                for(Block block:blocks) { 
                    setTypeColor(g, block.getType()); 
                    g.fillRect((block.getX()*Block.SIZE)-Block.SIZE, (block.getY()*Block.SIZE)-Block.SIZE,Block.SIZE,Block.SIZE); 
                     
                    setTypeColorDarker(g, 7); 
                    g.drawRect((block.getX()*Block.SIZE)-Block.SIZE, (block.getY()*Block.SIZE)-Block.SIZE,Block.SIZE,Block.SIZE); 
             
                } 
            } 
             
            public void setX(int x) { 
                this.x=x; 
            } 
            public void setY(int y) { 
                this.y=y; 
            } 
             
            public int getX() { 
                return x; 
            } 
            public int getY() { 
                return y; 
            } 
            public int getType() { 
                return type; 
            } 
        } 
         
  
    class Shape { 
        Block [] nowBlocks; 
        Random random; 
        int type; 
        int [][] shapeCoord; 
         
        public Shape() { 
            random=new Random(); 
            type=random.nextInt(7);    //7개의 도형 종류 
            if(type==7) type=2;     //길쭉이의 확률을 높여줌 
            nowBlocks=new Block[4]; 
            shapeCoord=new int[4][2];//shapeCoord는 원시좌표를 의미 
             
             
             
            System.arraycopy(SHAPE_COORD[type], 0, shapeCoord, 0, SHAPE_COORD[type].length); 
            for(int i=0; i<4; i++) { 
                nowBlocks[i]=new Block(type); 
                nowBlocks[i].setX(shapeCoord[i][0]+NUM_OF_BLOCKS_HOR/2); 
                nowBlocks[i].setY(shapeCoord[i][1]+2); 
            } 
             
             
             
             
        } 
         
         
        public void tryRotate() { 
            boolean canRotate=true; 
            if(getType()==6) { 
                return;   
            } 
             
             
            int [][] tempPoint=new int[4][2]; 
             
            for(int i=0; i<4; i++) { 
                tempPoint[i][0]=-shapeCoord[i][1];//바뀐 원시 좌표의 X 
                tempPoint[i][1]=shapeCoord[i][0];    //바뀐 원시 좌표의 Y 
  
                 
                if(isBlockAt(tempPoint[i][0]+nowBlocks[0].getX(),tempPoint[i][1]+nowBlocks[0].getY())) { 
                    canRotate=false; 
                    break; 
                } 
            } 
             
            if(canRotate) { //for문을 견뎌내고 회전 가능해졌을 경우 
                for(int i=0; i<4; i++) { 
                    shapeCoord[i][0]=tempPoint[i][0]; 
                    shapeCoord[i][1]=tempPoint[i][1]; 
                    //바뀐 원시 COORD좌표 
                     
                    nowBlocks[i].setX(shapeCoord[i][0]+nowBlocks[0].getX()); 
                    nowBlocks[i].setY(shapeCoord[i][1]+nowBlocks[0].getY()); 
                     
                    virtualShape.setLocation(nowBlocks); 
                     
                    repaint(); 
                } 
            } 
            else System.out.println("unable to rotate "); 
        } 
         
  
         
         
         
        public int [] getEffectiveYs() { 
            int [] tempArr= {100,100,100,100}; 
            int temp=0, length=0; 
            boolean used=false; 
             
            for(int i=0; i<4; i++) { 
                used=false;; 
                 
                for (int j=0; j<i; j++) { 
                    if(nowBlocks[i].getY()==tempArr[j]) { 
                        used=true; break; 
                    } 
                } 
                 
                if(!used) { 
                    tempArr[i]=nowBlocks[i].getY(); 
                    length++; 
                } 
                //i번째 y값이 중복이 아니면 입력되고, 
                //중복이면 100인채로 패스 
            } 
             
            for(int i=0; i<3; i++) { 
                for(int j=i; j<4; j++) { 
                    if(tempArr[i]>tempArr[j]) { 
                        temp=tempArr[i]; 
                        tempArr[i]=tempArr[j]; 
                        tempArr[j]=temp; 
                    } 
                } 
            } 
             
            int []resultArr=new int[length]; 
            for(int i=0; i<length; i++) { 
                resultArr[i]=tempArr[i]; 
            } 
             
             
            return resultArr; 
        } 
         
        public int getType() { 
            return type; 
        } 
         
        public void draw(Graphics g) { 
                for(Block block:nowBlocks) { 
                    setTypeColorDarker(g, block.getType()); 
                    g.fillRect((block.getX()*Block.SIZE)-Block.SIZE,(block.getY()*Block.SIZE)-Block.SIZE,Block.SIZE, Block.SIZE); 
                    setTypeColor(g, block.getType()); 
                    g.fillRect((block.getX()*Block.SIZE)-Block.SIZE+2,(block.getY()*Block.SIZE)-Block.SIZE+2,Block.SIZE-4, Block.SIZE-4); 
                } 
             
        } 
     
     
    public boolean isFallen() { 
        boolean temp=false; 
         
        for(Block block:nowBlocks) { 
            if(block.isFallen()) { 
                temp=true; 
                break; 
            } 
        } 
         
        return temp; 
    } 
     
    public boolean canMove(int deltax, int deltay) { 
        boolean isOccupied=false; 
         
        for(Block block:nowBlocks) { 
            if(isBlockAt(block.getX()+deltax,block.getY()+deltay)) { 
                isOccupied=true; 
                break; 
            } 
        } 
         
        return !isOccupied; 
    } 
     
    public void Move(int deltax, int deltay) { 
        for(Block block:nowBlocks) { 
            block.tryMove(deltax, deltay); 
        } 
    } 
     
    public void drop() { 
        while(!isFallen()) { 
            Move(0,1); 
        } 
    } 
         
         
        
         
    } 
     
    class VirtualShape extends Shape { 
         
        public VirtualShape(Block [] nowBlocks) { 
            for(int i=0; i<4; i++) { 
                this.nowBlocks[i]=new Block(nowBlocks[i].getX(), nowBlocks[i].getY()); 
            } 
            drop();    //shape랑 같은 좌표로 생성된 뒤 떨궈버려. 
        } 
         
        public void draw(Graphics g) { 
             
            for(Block block:nowBlocks) { 
                setTypeColorDarker(g,8); 
                g.fillRect((block.getX()*Block.SIZE)-Block.SIZE,(block.getY()*Block.SIZE)-Block.SIZE,Block.SIZE, Block.SIZE); 
                setTypeColor(g,8); 
                g.fillRect((block.getX()*Block.SIZE)-Block.SIZE+2,(block.getY()*Block.SIZE)-Block.SIZE+2,Block.SIZE-4, Block.SIZE-4); 
            } 
        } 
         
        public void setLocation(Block [] nowBlocks) { 
            //System.arraycopy(nowBlocks, 0, this.nowBlocks, 0, nowBlocks.length); 
            //위 에 놈은 왜 복사가 안대냐 - 얕은 복사 
             
            for(int i=0; i<4; i++) { 
                this.nowBlocks[i]=new Block(nowBlocks[i].getX(), nowBlocks[i].getY()); 
            } 
            drop(); 
        } 
         
         
         
    } 
     
    public void newGame() { 
        isAlive=true; 
        score=0; 
        blocks=new ArrayList<Block>(); 
        genShape(); 
        remove(msg); 
        blockDownThread=new BlockDownThread(); 
        blockDownThread.start(); 
    } 
    public boolean isGameOver() { 
        for(int i=0; i<4; i++) { 
            if(blocks.get(blocks.size()-1-i).getY()<=4)    //긴 막대의 경우 4이므로 
                return true; 
                
                 
        } 
        return false; 
    } 
     
    public boolean isBlockAt(int x, int y) {    //(x,y)에 블락이 있으면 true 
        boolean temp=false; 
        for(Block block:blocks) { 
            if(block.x==x && block.y==y) { 
                temp=true; 
               
            } 
        } 
         
        if(x==0 || x==NUM_OF_BLOCKS_HOR+1)    temp=true; 
        if(y==NUM_OF_BLOCKS_VER+1) temp=true; 
        return temp; 
    } 
     
    public boolean isLineFull(int y) { 
        boolean temp=false; 
        int count=0; 
        for(Block block:blocks) { 
            if(block.getY()==y) count++; 
        } 
         
        if(count==NUM_OF_BLOCKS_HOR) { 
            return true; 
        } 
        else return false; 
         
    } 
     
    //y번째 줄의 블럭을 없앤다. 
    public void removeLine(int y) { 
        int sizeBefore=blocks.size(); 
        int count=0; 
        int index=0; 
        while(count<NUM_OF_BLOCKS_HOR) { 
            if(blocks.get(index).getY()==y) { 
                blocks.remove(index); 
                count++; 
            } 
             
            else index++; 
        } 
         
        int size=blocks.size(); 

        for(int i=0; i<size; i++) { 
            if(blocks.get(i).getY()<y) { 
                blocks.get(i).setY(blocks.get(i).getY()+1); 
            } 
        } 
        increaseScore(500); 
    } 
     
    public void increaseScore(int plus) { 
        score+=plus; 
        scoreLabel.setText("SCORE : "+score); 
    } 
     
    //7-BLACK 
    public void setTypeColor(Graphics g, int type) { 
        switch(type) { 
        case 0:g.setColor(Color.BLUE); break; 
        case 1:g.setColor(Color.CYAN);break; 
        case 2:g.setColor(Color.GREEN);break; 
        case 3:g.setColor(Color.PINK);break; 
        case 4:g.setColor(Color.ORANGE);break; 
        case 5:g.setColor(Color.WHITE);break; 
        case 6:g.setColor(Color.YELLOW);break; 
        case 7:g.setColor(Color.LIGHT_GRAY);break; 
      //VirtualShape 
        case 8:g.setColor(Color.DARK_GRAY);break; 
        } 
    } 
     
    public void setTypeColorDarker(Graphics g, int type) { 
        switch(type) { 
        case 0:g.setColor(Color.BLUE.darker()); break; 
        case 1:g.setColor(Color.CYAN.darker());break; 
        case 2:g.setColor(Color.GREEN.darker());break; 
        case 3:g.setColor(Color.PINK.darker());break; 
        case 4:g.setColor(Color.ORANGE.darker());break; 
        case 5:g.setColor(Color.WHITE.darker());break; 
        case 6:g.setColor(Color.YELLOW.darker());break; 
        case 7:g.setColor(Color.LIGHT_GRAY.darker());break; 
         
        //VirtualShape 
        case 8:g.setColor(Color.DARK_GRAY.darker());break; 
        //        case 7:g.setColor(Color.BLACK.darker());break; 
        } 
    } 
     
    public void drawBorder(Graphics g) { 
        setTypeColor(g, 7); 
        g.drawRect(0,0,Block.SIZE*NUM_OF_BLOCKS_HOR,Block.SIZE*NUM_OF_BLOCKS_VER); 
        g.drawLine(0, 4*Block.SIZE, Block.SIZE*NUM_OF_BLOCKS_HOR,4*Block.SIZE); 
    } 
     
  
     
     
    public void genShape() { 
        shape=new Shape(); 
        virtualShape=new VirtualShape(shape.nowBlocks); 
        increaseScore(100); 
    } 
     
    public void paintComponent(Graphics g) { 
        if(isAlive) { 
            super.paintComponent(g); 
             
            virtualShape.draw(g); 
            shape.draw(g); 
             
            for(Block block:blocks) { 
                block.draw(g); 
            } 
            drawBorder(g); 
        } 
         
        else { 
            super.paintComponent(g);    
        } 
         
    } 
     
     
}
