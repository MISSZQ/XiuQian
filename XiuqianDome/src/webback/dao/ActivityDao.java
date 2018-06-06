package webback.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import webback.bean.*;

import java.util.*;

public class ActivityDao {
    @Autowired
    private SessionFactory sessionFactory;
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    //查询所有活动
    public List<Activity> selectAllActivities(){
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("from Activity");
        List<Activity> activitys = query.list();
        return activitys;
    }

    //查询该userId创建的活动
    public List<Activity> selectActivitiesByUserId(int userId) {
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("from Activity where activityStartUser.userId = ?");
        query.setParameter(0, userId);
        List<Activity> activities = query.list();
        return activities;
    }

    //用户创建活动
    public Boolean insertnewActivity(Activity activity,String userName,String punishName){
        Session session = sessionFactory.openSession();
        Transaction tx=session.beginTransaction();
        try {
            Query query1 = session.createQuery("from User where userName=?");
            query1.setParameter(0,userName);
            User user=(User) query1.uniqueResult();
            Query query2 = session.createQuery("from Punish where punishName=?");
            query2.setParameter(0,punishName);
            Punish punish=(Punish) query2.uniqueResult();
            System.out.println(punish.getPunishId());
            String sql="\n" +
                    "insert into Activity(activityTitle,activityIntroduce,activityImageUrl,activityStartTime,activityEndTime,activityModel,activityUserNumber,activityStartUser,activityAttendNum,punishId)\n" +
                    "values ('"+activity.getActivityTitle()+"','"+activity.getActivityIntroduce()+"','"+activity.getActivityImageUrl()+"','"+activity.getActivityStartTime()+"','"+activity.getActivityEndTime()+"',1,"+activity.getActivityUserNumber()+","+user.getUserId()+",0,"+punish.getPunishId()+")";
            Query query = session.createSQLQuery(sql);
            query.executeUpdate();
            tx.commit();
            session.close();
            return true;
        }catch (Exception e){
            session.close();
            return false;
        }

    }

    //参加人数变更
    public Boolean updateAttendNum(int userId,int activityId){
        Session session = sessionFactory.openSession();
        Transaction tx=session.beginTransaction();

        try {
            Activity activity1=session.get(Activity.class,activityId);
            activity1.setActivityAttendNum(activity1.getActivityAttendNum()+1);
            session.update(activity1);
            String sql="insert into action(userId,activityId,signInTime)" +
                    "values("+userId+","+activityId+",0)";
            Query query = session.createSQLQuery(sql);
            query.executeUpdate();
            tx.commit();
            session.close();
            return true;
        }catch (Exception e){
            session.close();
            return false;
        }

    }

    //活动签到天数变更
    public Boolean updateSigninTime(int userId,int activityId){
        Session session = sessionFactory.openSession();
        Transaction tx=session.beginTransaction();
        try {
            Query query = session.createQuery("from Action where userId.userId=? and activityId.activityId=?");
            query.setParameter(0,userId);
            query.setParameter(1,activityId);
            Action action=(Action) query.uniqueResult();
            action.setSignInTime(action.getSignInTime()+1);
            session.update(action);
            tx.commit();
            session.close();
            return true;
        }catch (Exception e){
            session.close();
            return false;
        }
    }

    //某用户对某活动发表的评论
    public Boolean userpulishDiscuss(String userName, String activityTitle, Discuss discuss){
        Session session = sessionFactory.openSession();
        Transaction tx=session.beginTransaction();
        try {
            Query query = session.createQuery("from Action where userId.userName=? and activityId.activityTitle=?");
            query.setParameter(0,userName);
            query.setParameter(1,activityTitle);
            Action action=(Action) query.uniqueResult();
            String sql="insert into Discuss(actionId,discussIntroduce,discussImageUrl,discussPraise,discussTime)" +
                    "values ("+action.getActionId()+",'"+discuss.getDiscussIntroduce()+"','"+discuss.getDiscussImageUrl()+"',"+
                    "0,'"+discuss.getDiscussTime()+"')";
            Query query1 = session.createSQLQuery(sql);
            query1.executeUpdate();
            tx.commit();
            session.close();
            return true;
        }catch (Exception e){
            session.close();
            return false;
        }
    }

    //某用户对某活动发表的某条评论点赞数的变更
    public Boolean discussPraiseUpdate(int userId,int activityId,int discussId){
        Session session = sessionFactory.openSession();
        Transaction tx=session.beginTransaction();
        try{
            Query query = session.createQuery("from Discuss where actionId.userId.userId=? and actionId.activityId.activityId=? and discussId=?");
            query.setParameter(0,userId);
            query.setParameter(1,activityId);
            query.setParameter(2,discussId);
            Discuss discuss=(Discuss) query.uniqueResult();
            discuss.setDiscussPraise(discuss.getDiscussPraise()+1);
            session.update(discuss);
            tx.commit();
            session.close();
            return true;
        }catch (Exception e){
            session.close();
            return false;
        }
    }

    //某用户参加的所有活动
    public List<Activity> selectAuserActivity(int userId){
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("from Action where userId.userId=?");
        query.setParameter(0,userId);
        List<Action> actions=query.list();
        System.out.println("actions的长度"+actions.size());
        List<Activity> activities=new ArrayList<>();
        for (int i=0;i<actions.size();i++){
           activities.add(actions.get(i).getActivityId());
        }
        session.close();
        return activities;
    }

    //查找某活动,条件输入的信息在活动名中存在
    public List<Activity> findActivityByStr(String activityStr){
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("FROM Activity WHERE activityTitle LIKE '%"+activityStr+"%'");
        List<Activity> activities=query.list();
        System.out.println(activities.size());
        session.close();
        return activities;
    }

    //查询对于某活动发表的所有评论
    public List<Discuss> selectAllDisscussByactivityId(int activityId){
        Session session = sessionFactory.openSession();
        Query query1 = session.createQuery("from Discuss where actionId.activityId.activityId=?");
        query1.setParameter(0,activityId);
        List<Discuss> discusses=query1.list();
        System.out.println(discusses.size());
        session.close();
        return discusses;

    }

    //排行榜1（最热活动）
    public List<Activity> hotActivity(){
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("from Activity order by activityAttendNum desc ");
        List<Activity> activitys = query.list();
        session.close();
        return activitys;
    }

    //排行榜2（签到数）
    public List<Action> getSigninmostUser(){
        List<Action> actionList=new ArrayList<>();
        List<User> userList=new ArrayList<>();
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("FROM User ");
        List<User> users=query.list();
        for (int i=0;i<users.size();i++){
           // Query query2 = session.createQuery("FROM Action where userId.userId=?");
            //List<Action> asd=query2.list();
            Query query1 = session.createQuery("FROM Action where userId.userId=? order by signInTime desc ");
            query1.setParameter(0,users.get(i).getUserId());
            List<Action> actions=query1.list();
            if (actions.size()>0) {
                actionList.add(actions.get(0));
            }

        }
        Collections.sort(actionList, new Comparator<Action>() {
            @Override
            public int compare(Action o1, Action o2) {
                if (o1.getSignInTime()>o2.getSignInTime()){
                    return -1;
                }
                if (o1.getSignInTime()==o2.getSignInTime()){
                    return 0;
                }
                return 1;
            }
        });
        session.close();
        return actionList;
    }

    //排行榜3（点赞数）
    public List<User> getPraisemostUser(){
        List<User> userList=new ArrayList<>();
        List<int[]> praiseSum=new ArrayList<>();
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("FROM User ");
        List<User> users=query.list();
        for (int i=0;i<users.size();i++){
            int signSum=0;
            int[] asd=new int[2];
            Query query1 = session.createQuery("from Discuss where actionId.userId.userId=?");
            query1.setParameter(0,users.get(i).getUserId());
            List<Discuss> discusses=query1.list();
            if(discusses.size()>0) {
                asd[0] = users.get(i).getUserId();
                for (int a = 0; a < discusses.size(); a++) {
                    signSum = signSum + discusses.get(a).getDiscussPraise();
                }
                asd[1] = signSum;
                praiseSum.add(asd);
            }
        }
        Collections.sort(praiseSum, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                if (o1[1]>o2[1]){
                    return -1;
                }
                if (o1[1]==o2[1]){
                    return 0;
                }
                return 1;
            }
        });
        for (int b=0;b<praiseSum.size();b++){
            Query query2 = session.createQuery("from User where userId=?");
            query2.setParameter(0,praiseSum.get(b)[0]);
            User user=(User) query2.uniqueResult();
            userList.add(user);
        }
        session.close();
        return userList;
    }

}
