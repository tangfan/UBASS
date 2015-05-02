package com.unionpay.upa.excel.export;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.unionpay.upa.excel.export.utils.FileUtils;

/**
 * 导出无效订单
 */
public class InvalidorderstatExport<T> extends AbstractExportExcel<T> {

    private static final String AREA_CODE_PATH = "/areaCode.properties";

    private static final String AREA_INSIDE = "境内";
    private static final String AREA_OUTSIDE = "境外";

	@Override
	public void exportExcel(Collection<T> dataset, InputStream in,
			OutputStream out) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Properties prop = new Properties();
        try {
            prop.load(this.getClass().getResourceAsStream(AREA_CODE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String areaCode = prop.getProperty("areaCode");

        super.exportExcel(dataset, in, out);

		// 读取工作表3
		Sheet sheet = workbook.getSheetAt(2);
		Row row = null;
		// 遍历集合数据，产生数据行
		Iterator<T> it = dataset.iterator();
		int index = 2;//从第四行开始写
		while (it.hasNext()) {
			++index;
			row = sheet.createRow(index);
			T t = (T) it.next();
			// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
			Field[] fields = t.getClass().getDeclaredFields();

			//本来只要<fields.length就好了，这样做事为了多循环一次，设置每一行后面的汇总信息
			for (short i = 0; i <= fields.length; i++) {
				// model中设置的所有属性
				if (i < fields.length) {
                    //境内外
                    if(i==1){
                        String sacqcode = (String)getValueByField(t,fields[3]);
                        String matchStr = sacqcode.substring(sacqcode.length()-4);
                        //根据收单机构号后四位来区分境内外
                        if(areaCode.indexOf(matchStr)>0){
                            setField(t,fields[i],AREA_OUTSIDE);
                        }else{
                            setField(t,fields[i],AREA_INSIDE);
                        }
                    }
                    super.writeToCell(row.createCell(i), fields[i], t);
				} else {//用于每一行最后的一些汇总信息
					//int formulaIndex = index + 1;
					// 设置后面的函数
					//Cell cardSupport = row.createCell(fields.length);
					//cardSupport.setCellFormula("SUM(C" + formulaIndex + ",E" + formulaIndex + ",G" + formulaIndex + ",I" + formulaIndex + ",K" + formulaIndex + ",M" + formulaIndex + ",O" + formulaIndex + ",Q" + formulaIndex + ",S" + formulaIndex + ",U" + formulaIndex + ",W" + formulaIndex + ",Y" + formulaIndex + ",AA" + formulaIndex + ",AC" + formulaIndex + ",AE" + formulaIndex + ",AG" + formulaIndex + ",AI" + formulaIndex + ",AK" + formulaIndex + ",AM" + formulaIndex + ",AO" + formulaIndex + ",AQ" + formulaIndex + ",AS" + formulaIndex + ",AU" + formulaIndex + ")");
				}
			}
		}
		try {
			workbook.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			FileUtils.close(out);
			FileUtils.close(in);
		}

	}

}
