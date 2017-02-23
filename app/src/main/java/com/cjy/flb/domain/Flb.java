package com.cjy.flb.domain;

/**
 * Created by Administrator on 2015/12/15 0015.
 */
public class Flb {

    /**
     * status : success
     * code : 1
     */

    private ResponseEntity response;

    public void setResponse(ResponseEntity response) {
        this.response = response;
    }

    public ResponseEntity getResponse() {
        return response;
    }

    public static class ResponseEntity {
        private String status;
        private int code;
        private String failure_index;

        public String getFailure_index() {
            return failure_index;
        }

        public void setFailure_index(String failure_index) {
            this.failure_index = failure_index;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getStatus() {
            return status;
        }

        public int getCode() {
            return code;
        }
    }
}
