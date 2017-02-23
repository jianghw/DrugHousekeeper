package com.cjy.flb.bean;

/**
 * Created by jianghw on 2016/4/6 0006.
 * Description
 */
public class BoxVisitTimeBean {


    /**
     * id : 1
     * device_uid : 10000001
     * device_sn : 111111
     * bound_activate_flag : true
     * created_at : 2015-12-19T01:28:39.000+08:00
     * updated_at : 2016-03-22T10:39:36.715+08:00
     * last_visit : 2016-03-26T11:52:54.297+08:00
     * first_visit_time : 2016-03-22T10:39:17.306+08:00
     */

    private ResponseEntity response;

    public ResponseEntity getResponse()
    {
        return response;
    }

    public void setResponse(ResponseEntity response)
    {
        this.response = response;
    }

    public static class ResponseEntity {
        private int id;
        private String device_uid;
        private String device_sn;
        private boolean bound_activate_flag;
        private String created_at;
        private String updated_at;
        private String last_visit;
        private String first_visit_time;

        public int getId()
        {
            return id;
        }

        public void setId(int id)
        {
            this.id = id;
        }

        public String getDevice_uid()
        {
            return device_uid;
        }

        public void setDevice_uid(String device_uid)
        {
            this.device_uid = device_uid;
        }

        public String getDevice_sn()
        {
            return device_sn;
        }

        public void setDevice_sn(String device_sn)
        {
            this.device_sn = device_sn;
        }

        public boolean isBound_activate_flag()
        {
            return bound_activate_flag;
        }

        public void setBound_activate_flag(boolean bound_activate_flag)
        {
            this.bound_activate_flag = bound_activate_flag;
        }

        public String getCreated_at()
        {
            return created_at;
        }

        public void setCreated_at(String created_at)
        {
            this.created_at = created_at;
        }

        public String getUpdated_at()
        {
            return updated_at;
        }

        public void setUpdated_at(String updated_at)
        {
            this.updated_at = updated_at;
        }

        public String getLast_visit()
        {
            return last_visit;
        }

        public void setLast_visit(String last_visit)
        {
            this.last_visit = last_visit;
        }

        public String getFirst_visit_time()
        {
            return first_visit_time;
        }

        public void setFirst_visit_time(String first_visit_time)
        {
            this.first_visit_time = first_visit_time;
        }
    }
}
