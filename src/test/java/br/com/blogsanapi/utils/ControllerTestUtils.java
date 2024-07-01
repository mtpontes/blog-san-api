package br.com.blogsanapi.utils;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class ControllerTestUtils {

    public static ResultActions postRequest(MockMvc mvc, String url, String requestBody) throws Exception {
        return mvc.perform(
            MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));
    }
    public static ResultActions patchRequest(MockMvc mvc, String url, String requestBody) throws Exception {
        return mvc.perform(
            MockMvcRequestBuilders.patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));
    }

    public static ResultActions getRequest(MockMvc mvc, String url) throws Exception {
        return mvc.perform(
            MockMvcRequestBuilders.get(url)
                .contentType(MediaType.APPLICATION_JSON));
    }
}