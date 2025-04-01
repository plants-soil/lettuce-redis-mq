package com.plantssoil.mq;

import java.util.Date;

public class TestBean {
	private String name;
	private int code;
	private Date d;
	private TestProperty p;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Date getD() {
		return d;
	}

	public void setD(Date d) {
		this.d = d;
	}

	public TestProperty getP() {
		return p;
	}

	public void setP(TestProperty p) {
		this.p = p;
	}

	@Override
	public String toString() {
		return "TestBean [name=" + name + ", code=" + code + ", d=" + d + ", p=" + p + "]";
	}
}
