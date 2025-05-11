package world.ssafy.tourtalk.model.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import world.ssafy.tourtalk.model.dto.Member.Status;

public class StatusTypeHandler extends BaseTypeHandler<Status>{

	// Java -> DB, enum을 DB에 저장, insert, update
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Status parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setString(i, parameter.name());
	}

	// DB -> Java, select 결과 읽기, 일반 조회
	@Override
	public Status getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : Status.valueOf(value);
	}

	// DB -> Java, select 결과 읽기, 컬럼 인덱스로 처리
	@Override
	public Status getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : Status.valueOf(value);
	}

	// DB -> Java, CALLABLE 결과 읽기, 저장 프로시저
	@Override
	public Status getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : Status.valueOf(value);
	}

}
