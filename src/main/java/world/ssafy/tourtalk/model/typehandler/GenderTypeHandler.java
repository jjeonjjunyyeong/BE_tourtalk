package world.ssafy.tourtalk.model.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import world.ssafy.tourtalk.model.dto.MemberDetails.Gender;

import java.sql.*;

public class GenderTypeHandler extends BaseTypeHandler<Gender> {

	// Java -> DB, enum을 DB에 저장, insert, update
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Gender parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name()); 
    }

	// DB -> Java, select 결과 읽기, 일반 조회
    @Override
    public Gender getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : Gender.valueOf(value); 
    }

	// DB -> Java, select 결과 읽기, 컬럼 인덱스로 처리
    @Override
    public Gender getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : Gender.valueOf(value);
    }

	// DB -> Java, CALLABLE 결과 읽기, 저장 프로시저
    @Override
    public Gender getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : Gender.valueOf(value);
    }
}