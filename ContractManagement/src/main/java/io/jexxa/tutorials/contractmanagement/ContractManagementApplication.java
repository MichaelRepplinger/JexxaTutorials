package io.jexxa.tutorials.contractmanagement;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.ObjectStoreManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.imdb.IMDBObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.jdbc.JDBCObjectStore;
import io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;
import io.jexxa.tutorials.contractmanagement.applicationservice.ContractService;
import io.jexxa.utils.JexxaLogger;
import org.apache.commons.cli.*;

public class ContractManagementApplication
{
    public static void main(String[] args)
    {
        // Define the default strategies via command line.
        // In this tutorial we use an ObjectStore which is either an IMDB database or a JDBC based repository.
        ObjectStoreManager.setDefaultStrategy(getRepositoryStrategy(args));

        var jexxaMain = new JexxaMain(ContractManagementApplication.class.getSimpleName());

        //print some application information
        JexxaLogger.getLogger(ContractManagementApplication.class)
                .info( "{}", jexxaMain.getBoundedContext().getContextVersion() );

        jexxaMain
                //Define which outbound ports should be managed by Jexxa
                .addDDDPackages(ContractManagementApplication.class)

                .bind(RESTfulRPCAdapter.class).to(ContractService.class)

                .start()

                .waitForShutdown()

                .stop();
    }

    // Methods for command line parsing
    static Options getOptions()
    {
        var options = new Options();
        options.addOption("j", "jdbc", false, "jdbc driven adapter strategy");
        return options;
    }


    @SuppressWarnings("rawtypes")
    private static Class<? extends IObjectStore> getRepositoryStrategy(String[] args)
    {
        if (jdbcParameterAvailable(args))
        {
            JexxaLogger.getLogger(ContractManagementApplication.class).info("Use persistence strategy: {} ", JDBCObjectStore.class.getSimpleName());
            return JDBCObjectStore.class;
        }

        JexxaLogger.getLogger(ContractManagementApplication.class).info("Use persistence strategy: {} ", IMDBObjectStore.class.getSimpleName());
        return IMDBObjectStore.class;
    }

    static boolean jdbcParameterAvailable(String[] args)
    {
        CommandLineParser parser = new DefaultParser();
        try
        {
            CommandLine line = parser.parse( getOptions(), args );

            return line.hasOption("jdbc");
        }
        catch( ParseException exp ) {
            JexxaLogger.getLogger(ContractManagementApplication.class)
                    .error( "Parsing failed.  Reason: {}", exp.getMessage() );
        }
        return false;
    }


    private ContractManagementApplication()
    {
        //Private constructor since we only offer main
    }

}
