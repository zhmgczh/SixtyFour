import javax.swing.*;
import java.awt.*;
import java.util.Random;
class Panel extends JPanel
{
    private static final String[]signs={"坤","剝","比","觀","豫","晉","萃","否","謙","艮","蹇","漸","小過","旅","鹹","遁","師","蒙","坎","渙","解","未濟","困","訟","升","蠱","井","巽","恒","鼎","大過","姤","複","頤","屯","益","震","噬嗑","隨","無妄","明夷","賁","既濟","家人","豐","離","革","同人","臨","損","節","中孚","歸妹","睽","兌","履","泰","大畜","需","小畜","大壯","大有","夬","乾"};
    private int current=0;
    private int current_status[]={-1,-1,-1};
    private int result[]={-1,-1,-1,-1,-1,-1};
    public Panel()
    {
        super(null);
    }
    public int get_current()
    {
        return current;
    }
    public void set_current_status(int current_status[])
    {
        this.current_status=current_status;
        int positive_num=0;
        for(int i=0;i<3;++i)
        {
            if(current_status[i]==1)
            {
                ++positive_num;
            }
        }
        int res;
        switch(positive_num)
        {
            case 0:
                res=2;
                break;
            case 1:
                res=1;
                break;
            case 2:
                res=0;
                break;
            default:
                res=3;
        }
        result[current++]=res;
        if(current==6)
        {
            SixtyFour.finished=true;
        }
    }
    @Override
    public void paint(Graphics graphics)
    {
        super.paint(graphics);
        for(int i=0;i<3;++i)
        {
            if(current_status[i]!=-1)
            {
                graphics.setColor(current_status[i]==0?Color.WHITE:Color.BLACK);
                graphics.fillOval(200+i*150,200,50,50);
            }
            graphics.setColor(Color.BLACK);
            graphics.drawOval(200+i*150,200,50,50);
        }
        graphics.drawString("占卜",212,300);
        graphics.drawString("本卦",362,300);
        graphics.drawString("變卦",512,300);
        for(int i=0;i<current;++i)
        {
            if(result[i]>1)
            {
                graphics.setColor(Color.RED);
            }
            else
            {
                graphics.setColor(Color.BLACK);
            }
            if(result[i]%2==0)
            {
                graphics.fillRect(175,450-i*25,45,10);
                graphics.fillRect(230,450-i*25,45,10);
            }
            else
            {
                graphics.fillRect(175,450-i*25,100,10);
            }
        }
        if(current==6)
        {
            graphics.setColor(Color.BLACK);
            int origin=0,change=0;
            for(int i=0;i<6;++i)
            {
                if(result[i]%2==0)
                {
                    graphics.fillRect(325,450-i*25,45,10);
                    graphics.fillRect(380,450-i*25,45,10);
                    if(result[i]==2)
                    {
                        graphics.fillRect(475,450-i*25,100,10);
                        change|=1<<(5-i);
                    }
                    else
                    {
                        graphics.fillRect(475,450-i*25,45,10);
                        graphics.fillRect(530,450-i*25,45,10);
                    }
                }
                else
                {
                    graphics.fillRect(325,450-i*25,100,10);
                    origin|=1<<(5-i);
                    if(result[i]==1)
                    {
                        graphics.fillRect(475,450-i*25,100,10);
                        change|=1<<(5-i);
                    }
                    else
                    {
                        graphics.fillRect(475,450-i*25,45,10);
                        graphics.fillRect(530,450-i*25,45,10);
                    }
                }
            }
            graphics.drawString(signs[origin],362,500);
            graphics.drawString(signs[change],512,500);
        }
    }
}
public class SixtyFour
{
    public static final String[]res_name={"初爻","二爻","三爻","四爻","五爻","上爻","完畢"};
    public static long last_time=0;
    public static boolean finished=false;
    public static void main(String[]args)
    {
        JFrame jFrame=new JFrame("卜卦（金錢卦）");
        java.net.URL imageURL=SixtyFour.class.getResource("Taiji.jpg");
        jFrame.setIconImage(new ImageIcon(imageURL).getImage());
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setSize(800,600);
        jFrame.setResizable(false);
        jFrame.setLocationRelativeTo(null);
        Panel panel=new Panel();
        JLabel question_label=new JLabel("欲卜之事：");
        question_label.setBounds(100,50,100,50);
        panel.add(question_label);
        JTextField question=new JTextField();
        question.setBounds(200,50,500,50);
        panel.add(question);
        JButton generator=new JButton("拋銅錢——"+res_name[panel.get_current()]);
        generator.setBounds(100,125,600,50);
        generator.addActionListener(e->
        {
            question.setEnabled(false);
            if(System.currentTimeMillis()-last_time<1000)
            {
                return;
            }
            String question_text=question.getText();
            long base=0,seed=0;
            for(int i=0;i<question_text.length();++i)
            {
                base=Math.max(base,(question_text.charAt(i)+1)%Integer.MAX_VALUE);
            }
            for(int i=0;i<question_text.length();++i)
            {
                seed=seed*base%Integer.MAX_VALUE;
                seed=(seed+question_text.charAt(i))%Integer.MAX_VALUE;
            }
            Random random=new Random((System.currentTimeMillis()+seed)%Long.MAX_VALUE);
            int results[]=new int[3];
            for(int i=0;i<3;++i)
            {
                results[i]=random.nextInt(2);
            }
            panel.set_current_status(results);
            panel.repaint();
            generator.setEnabled(false);
            generator.setText("拋銅錢——"+res_name[panel.get_current()]);
            last_time=System.currentTimeMillis();
        });
        panel.add(generator);
        jFrame.setContentPane(panel);
        jFrame.setVisible(true);
        while(true)
        {
            if(finished)
            {
                generator.setEnabled(false);
                break;
            }
            if(System.currentTimeMillis()-last_time>=1000)
            {
                generator.setEnabled(true);
            }
            try
            {
                Thread.sleep(200);
            }
            catch(InterruptedException e)
            {
                continue;
            }
        }
    }
}