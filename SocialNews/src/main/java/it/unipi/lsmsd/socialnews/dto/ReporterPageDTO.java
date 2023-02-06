package it.unipi.lsmsd.socialnews.dto;

import java.util.List;

public class ReporterPageDTO extends BaseDTO{
    ReporterDTO reporter;
    List<PostDTO> posts;
    Integer numOfFollower;

    public ReporterPageDTO(ReporterDTO reporter, List<PostDTO> posts, Integer numOfFollower) {
        this.reporter = reporter;
        this.posts = posts;
        this.numOfFollower = numOfFollower;
    }

    public ReporterDTO getReporter() {
        return reporter;
    }

    public void setReporter(ReporterDTO reporter) {
        this.reporter = reporter;
    }

    public List<PostDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDTO> posts) {
        this.posts = posts;
    }

    public Integer getNumOfFollower() {
        return numOfFollower;
    }

    public void setNumOfFollower(Integer numOfFollower) {
        this.numOfFollower = numOfFollower;
    }

    @Override
    public String toString() {
        return "ReporterPageDTO{" +
                "reporter=" + reporter +
                ", posts=" + posts +
                ", numOfFollower=" + numOfFollower +
                '}';
    }
}
