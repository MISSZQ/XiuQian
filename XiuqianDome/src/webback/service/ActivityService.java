package webback.service;

import org.springframework.beans.factory.annotation.Autowired;
import webback.bean.Action;
import webback.bean.Activity;
import webback.bean.Discuss;
import webback.bean.User;
import webback.dao.ActivityDao;

import java.util.List;


public class ActivityService {
    @Autowired
    private ActivityDao activityDao;

    public ActivityDao getActivityDao() {
        return activityDao;
    }

    public void setActivityDao(ActivityDao activityDao) {
        this.activityDao = activityDao;
    }

    public List<Activity> findActivities(){
        return activityDao.selectAllActivities();
    }

    public List<Activity> findActivitiesByUserId(int userId){
        return activityDao.selectActivitiesByUserId(userId);
    }

    public Boolean insertnewActivity(Activity activity,String userName,String punishName){ return activityDao.insertnewActivity(activity,userName,punishName);}

    public Boolean updateAttendNum(int userId,int activityId){ return activityDao.updateAttendNum(userId,activityId);}

    public Boolean updateSigninTime(int userId,int activityId){return activityDao.updateSigninTime(userId,activityId);}

    public Boolean userpulishDiscuss(String userName, String activityTitle, Discuss discuss){ return activityDao.userpulishDiscuss(userName,activityTitle,discuss);}

    public Boolean discussPraiseUpdate(int userId,int activityId,int discussId){return activityDao.discussPraiseUpdate(userId,activityId,discussId);}

    public List<Activity> selectAuserActivity(int userId){return activityDao.selectAuserActivity(userId);}

    public List<Activity> findActivityByStr(String activityStr){return activityDao.findActivityByStr(activityStr);}

    public List<Discuss> selectAllDisscussByactivityId(int activityId){return activityDao.selectAllDisscussByactivityId(activityId);}

    public List<Activity> hotActivity(){return activityDao.hotActivity();}

    public List<Action> getSigninmostUser(){return activityDao.getSigninmostUser();}

    public List<User> getPraisemostUser(){return activityDao.getPraisemostUser();}
}
