package application;

public class StandardResponse {
    private String status = "Success";
    private StringBuilder data = new StringBuilder();

    public void setStatus(String status) {
        this.status = status;
    }

    public StringBuilder getData() {
        return data;
    }

    public void setData(StringBuilder data) {
        this.data = data;
    }

    public void setData(String data) {
        this.data = new StringBuilder();
        this.data.append(data);
    }

    public String toJSON() {
        return "{\n" +
                "\"status\": \"" +
                status +
                "\",\n\"data\": " +
                data +
                "\n}";
    }
}
