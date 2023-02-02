package it.unipi.lsmsd.socialnews.dto;

import java.util.List;

public class ReporterPageDTO extends BaseDTO{
    ReporterDTO reporter;
    List<PostDTO> posts;

    public ReporterPageDTO(ReporterDTO reporter, List<PostDTO> posts) {
        this.reporter = reporter;
        this.posts = posts;
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

    @Override
    public String toString() {
        return "ReporterPageDTO{" +
                "reporter=" + reporter +
                ", posts=" + posts +
                '}';
    }
}
