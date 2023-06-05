package ua.com.sinenko.examples.web.dto;

import java.io.Serializable;

public class Dto implements Serializable {
    private String status;

    private InternalDto internalDto;

    public Dto() {
    }

    public Dto(String status, InternalDto internalDto) {
        this.status = status;
        this.internalDto = internalDto;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public InternalDto getInternalDto() {
        return internalDto;
    }

    public void setInternalDto(InternalDto internalDto) {
        this.internalDto = internalDto;
    }

    @Override
    public String toString() {
        return "Dto{" +
                "status='" + status + '\'' +
                ", internalDto=" + internalDto +
                '}';
    }
}
