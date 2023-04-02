package handson;

import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;



public class Task03c_SYNC_PROJECTS {

    public static void main(String[] args) throws IOException, InterruptedException {

        Logger logger = LoggerFactory.getLogger(Task03c_SYNC_PROJECTS.class.getName());

        // TODO
        //  Have docker installed
        //  Provide here source and target project prefixes in "dev.properties"
        //  Make sure, source and target project have proper setup (locales, countries, taxes...) e.g.:
        //  install sunrise data (https://github.com/commercetools/commercetools-sunrise-data)
        //  and remove products/customers/categories for SYNC task.
        //  See scripts in https://github.com/commercetools/commercetools-sunrise-data/blob/master/package.json

        final String sourcePrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();     // Your source api client prefix
        final String targetPrefix = ApiPrefixHelper.API_PROJECT_SYNC_CLIENT_PREFIX.getPrefix();    // Your target api client prefix

        Properties properties = new Properties();
        properties.load(ClientService.class.getResourceAsStream(ClientService.DEV_PROPERTIES_FILE_PATH));
        StringBuilder dockerRun = new StringBuilder();
        dockerRun.append("docker run ");
        dockerRun.append(" -e SOURCE_PROJECT_KEY=" + properties.getProperty(sourcePrefix + "projectKey"));
        dockerRun.append(" -e SOURCE_CLIENT_ID=" + properties.getProperty(sourcePrefix + "clientId"));
        dockerRun.append(" -e SOURCE_CLIENT_SECRET=" + properties.getProperty(sourcePrefix + "clientSecret"));
        dockerRun.append(" -e SOURCE_AUTH_URL=" + properties.getProperty(sourcePrefix + "authUrl"));
        dockerRun.append(" -e SOURCE_API_URL=" + properties.getProperty(sourcePrefix + "apiUrl"));
        dockerRun.append(" -e TARGET_PROJECT_KEY=" + properties.getProperty(targetPrefix + "projectKey"));
        dockerRun.append(" -e TARGET_CLIENT_ID=" + properties.getProperty(targetPrefix + "clientId"));
        dockerRun.append(" -e TARGET_CLIENT_SECRET=" + properties.getProperty(targetPrefix + "clientSecret"));
        dockerRun.append(" -e TARGET_AUTH_URL=" + properties.getProperty(targetPrefix + "authUrl"));
        dockerRun.append(" -e TARGET_API_URL=" + properties.getProperty(targetPrefix + "apiUrl"));

        // TODO
        //  Modify as wished
        //  RUN the project sync
        //
        dockerRun.append(" commercetools/commercetools-project-sync:5.3.1 -s products");
        logger.info(dockerRun.toString());

        Process process = Runtime.getRuntime().exec(dockerRun.toString());
        process.waitFor();
        logger.info(process.exitValue() + " ");

    }
}

