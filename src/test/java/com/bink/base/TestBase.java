package com.bink.base;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * @Desc 测试基准类，其他接口类继承本类
 * @Author by Bink.
 * Created on 2019/12/6.
 */
public class TestBase {

    public Logger logger;

    public static String CONTENT_TYPE_FORM = getWord("test_config", "CONTENT_TYPE_FORM");
    public static String CONTENT_TYPE_JSON = getWord("test_config", "CONTENT_TYPE_JSON");

    /**
     * 服务地址
     */
    public static String SERVER_HOST;
    /**
     * 端口号
     */
    public static String PORT;
    /**
     * 接口组地址
     */
    public static String BASE_PATH;
    public static String token;
    /**
     * 接口地址
     */
    public static String url;
    /**
     * 商铺用户及密码
     */
    public static String username = getWord("test_config", "SHOP_USERNAME");
    public static String password = getWord("test_config", "SHOP_PASSWORD");

    public static String TOKEN_PATH = "data.token";
    /**
     * 金币测试店铺编号
     */
    public static String shopIds = getWord("test_config", "shopIds");


    /**
     * 接口请求参数
     */
    public static Map<String, Object> paramsMap = new HashMap<>();

    /**
     * 配置测试环境
     */
    @BeforeClass
    public void setUp() {
        configLog();
        logger.info("配置测试环境参数");
        getConfig("test_config", "URL_B", "Port");
        logger.info(RestAssured.baseURI);
        RestAssured.registerParser("text/plain", Parser.JSON);
    }

    /**
     * 测试执行之后，重置环境
     */
    @AfterClass
    public void afterTest() {
        logger.info("+++++++测试完成，清理环境+++++++");
        resetBaseURI();
        resetBasePath();
        clearMap();
    }

    /**
     * 清空参数
     */
    public static void clearMap() {
        paramsMap.clear();
        url = "";
    }

    /**
     * 配置日志文件及类名称
     */
    public void configLog() {
        String className = this.getClass().getName();
        logger = Logger.getLogger(className);
        PropertyConfigurator.configure("log4j.properties");
        logger.setLevel(Level.DEBUG);
    }


    /**
     * 设置本类接口请求的Token
     *
     * @param loginUrl  登陆地址
     * @param paramsMap 请求参数
     * @param tokenPath 返回Token路径
     */
    public static void setToken(String loginUrl, Map<String, Object> paramsMap,
                                String tokenPath) {
        token = given().
                params(paramsMap).
                relaxedHTTPSValidation().
                post(loginUrl).
                path(tokenPath).toString();
        System.out.println("所需返回值为： ===>  " + token);

    }

    /**
     * 获取配置文件
     */
    public static void getConfig(String configFileName, String serverHost,
                                 String port) {
        ResourceBundle rb = ResourceBundle.getBundle(configFileName);
        SERVER_HOST = rb.getString(serverHost);
        PORT = rb.getString(port);
        setBaseURI();
    }

    /**
     * 获取配置文件内的某个值
     *
     * @param configFileName 配置文件名称
     * @param key            值对应的名称
     * @return keyWord 所需的值
     */
    public static String getWord(String configFileName, String key) {
        ResourceBundle rb = ResourceBundle.getBundle(configFileName);
        try {
            String keyWord = new String(rb.getString(key).getBytes("ISO-8859-1"), "UTF8");
            return keyWord;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        String keyWord = rb.getString(key);
        return null;
    }

    /**
     * 设置Base URI
     */
    public static void setBaseURI() {
        if ("80".equals(PORT)) {
            RestAssured.baseURI = SERVER_HOST;
        } else {
            RestAssured.baseURI = SERVER_HOST + ":" + PORT;
        }
    }

    /**
     * 设置Base Path
     *
     * @param basePath
     */
    public static void setBasePath(String configFileName, String basePath) {
        BASE_PATH = getWord(configFileName, basePath);
        RestAssured.basePath = BASE_PATH;
    }

    /**
     * 设置Base Path
     *
     * @param basePath
     */
    public static void setBasePath(String basePath) {
        RestAssured.basePath = basePath;
    }

    /**
     * 重置Base URI
     */
    public static void resetBaseURI() {
        RestAssured.baseURI = null;
    }

    /**
     * 重置Base Path
     */
    public static void resetBasePath() {
        RestAssured.basePath = null;
    }


    /**
     * 1、Post请求方法
     * 断言String类型关键字方法
     *
     * @param url          请求地址
     * @param paramsMap    参数
     * @param assertPath   要断言的关键字路径，如：data.Item.orderId
     * @param expectedWord 预期关键字
     */
    public static void postEquals(String url,
                                  Map<String, Object> paramsMap,
                                  String assertPath, String expectedWord) {
        given().contentType(CONTENT_TYPE_FORM).
                params(paramsMap).
                relaxedHTTPSValidation().
                when().
                post(url.trim()).
                then().
                log().ifError().
                assertThat().
                body(assertPath, equalTo(expectedWord));
    }

    /**
     * 3、Post请求方法
     * 断言boolean类型关键字方法
     *
     * @param url
     * @param paramsMap
     * @param assertPath
     * @param expectedWord
     */
    public static void postEquals(String url,
                                  Map<String, Object> paramsMap,
                                  String assertPath, boolean expectedWord) {

        given().
                contentType(CONTENT_TYPE_FORM).
                params(paramsMap).
                relaxedHTTPSValidation().
                when().
                post(url.trim()).
                then().
                log().ifError().
                body(assertPath, equalTo(expectedWord));
    }

    /**
     * 根据路径返回特定的值
     *
     * @param url
     * @param paramsMap
     * @param assertPath
     * @param expectedWord
     */
    public static void postEqual(String url, Map<String, Object> paramsMap,
                                 String assertPath, String expectedWord) {
        Response response = given().
                contentType(CONTENT_TYPE_FORM).
                params(paramsMap).relaxedHTTPSValidation().when().log().all().
                post(url);
        response.prettyPrint();
        String keyWord = response.getBody().path(assertPath).toString();
        System.out.println(keyWord);
        Assert.assertEquals(keyWord, expectedWord);
    }

    /**
     * 2、Post请求方法
     * 断言Int类型关键字方法
     *
     * @param url          请求地址
     * @param paramsMap    参数
     * @param assertPath   要断言的关键字路径，如：data.Item.orderId
     * @param expectedWord 预期关键字
     */
    public static void postEquals(String url,
                                  Map<String, Object> paramsMap,
                                  String assertPath, int expectedWord) {
        given().
                params(paramsMap).
                relaxedHTTPSValidation().
                when().
                post(url.trim()).
                then().
                log().ifError().
                assertThat().
                body(assertPath, equalTo(expectedWord));
    }

    /**
     * 获取一组数据，断言包含某一特殊字段
     *
     * @param url
     * @param statusCode
     * @param paramsMap
     * @param assertPath
     * @param expectedWord
     */
    public static void postHasItems(String url, int statusCode,
                                    Map<String, Object> paramsMap,
                                    String assertPath, String expectedWord) {

        given().
                contentType(CONTENT_TYPE_FORM).
                params(paramsMap).
                relaxedHTTPSValidation().
                when().
                post(url.trim()).
                then().
                log().ifError().
                assertThat().statusCode(statusCode).
                and().
                body(assertPath, equalTo(expectedWord));

    }

    /**
     * 获取接口返回值
     *
     * @param url         请求地址
     * @param paramsMap   参数
     * @param keywordPath 需要返回值的路径，如："data.token"
     * @return keyWord
     */
    public static String returnValue(String url, Map<String, Object> paramsMap,
                                     String keywordPath) {
        String keyWord = given().
                params(paramsMap).
                relaxedHTTPSValidation().
                post(url).
                path(keywordPath).toString();
        System.out.println("所需返回值为：" + keywordPath + "  ===>  " + keyWord);
        return keyWord;
    }

    /**
     * 打印消息返回体
     *
     * @param url       请求地址
     * @param paramsMap 参数
     */
    public static void print(String url, Map<String, Object> paramsMap) {
        Response response = given().
                contentType(CONTENT_TYPE_FORM).
                params(paramsMap).relaxedHTTPSValidation().when().log().all().
                post(url);
        response.prettyPrint();
    }

    /**
     * @param contentType 接口请求方式
     * @param url
     * @param paramsMap
     */
    public static void print(String contentType, String url,
                             Map<String, Object> paramsMap) {
        Response response = given().
                contentType(contentType).
                params(paramsMap).relaxedHTTPSValidation().
                post(url);
        response.prettyPrint();
    }
}
