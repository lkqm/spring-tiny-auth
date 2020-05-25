package com.github.lkqm.auth.test;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

import java.io.InputStream;
import java.sql.SQLException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DbUnitUtils {

    /**
     * 从类路径读取指定xml文件
     */
    public static IDataSet readFlatXmlDataSet(String xmlFile) throws DataSetException {
        InputStream is = DbUnitUtils.class.getClassLoader().getResourceAsStream(xmlFile);
        return new FlatXmlDataSetBuilder().build(is);
    }

    /**
     * 从新加载数据到db, 会清空数据库并插入数据
     */
    public static void reloadFlatXmlToDb(IDatabaseConnection connection, String xmlFile) throws DatabaseUnitException, SQLException {
        IDataSet dataSet = readFlatXmlDataSet(xmlFile);
        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
    }

}
