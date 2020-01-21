package com.newgen.model;

public class DataField {
	private String dataFieldIndex;
	private String dataFieldIndexName;
	private String dataFieldIndexType;
	private String dataFieldIndexLength;
	private String usefulInfoFlag;
	private String usefulInfoSize;
	private String indexAttribute;
	private String indexFlag;
	private String pickable;
	private String fieldOrder;

	public DataField() {
		super();
	}

	public String getDataFieldIndex() {
		return dataFieldIndex;
	}

	public void setDataFieldIndex(String dataFieldIndex) {
		this.dataFieldIndex = dataFieldIndex;
	}

	public String getDataFieldIndexName() {
		return dataFieldIndexName;
	}

	public void setDataFieldIndexName(String dataFieldIndexName) {
		this.dataFieldIndexName = dataFieldIndexName;
	}

	public String getDataFieldIndexType() {
		return dataFieldIndexType;
	}

	public void setDataFieldIndexType(String dataFieldIndexType) {
		this.dataFieldIndexType = dataFieldIndexType;
	}

	public String getDataFieldIndexLength() {
		return dataFieldIndexLength;
	}

	public void setDataFieldIndexLength(String dataFieldIndexLength) {
		this.dataFieldIndexLength = dataFieldIndexLength;
	}

	public String getUsefulInfoFlag() {
		return usefulInfoFlag;
	}

	public void setUsefulInfoFlag(String usefulInfoFlag) {
		this.usefulInfoFlag = usefulInfoFlag;
	}

	public String getUsefulInfoSize() {
		return usefulInfoSize;
	}

	public void setUsefulInfoSize(String usefulInfoSize) {
		this.usefulInfoSize = usefulInfoSize;
	}

	public String getIndexAttribute() {
		return indexAttribute;
	}

	public void setIndexAttribute(String indexAttribute) {
		this.indexAttribute = indexAttribute;
	}

	public String getIndexFlag() {
		return indexFlag;
	}

	public void setIndexFlag(String indexFlag) {
		this.indexFlag = indexFlag;
	}

	public String getPickable() {
		return pickable;
	}

	public void setPickable(String pickable) {
		this.pickable = pickable;
	}

	public String getFieldOrder() {
		return fieldOrder;
	}

	public void setFieldOrder(String fieldOrder) {
		this.fieldOrder = fieldOrder;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataFieldIndexName == null) ? 0 : dataFieldIndexName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataField other = (DataField) obj;
		if (dataFieldIndexName == null) {
			if (other.dataFieldIndexName != null)
				return false;
		} else if (!dataFieldIndexName.equals(other.dataFieldIndexName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataField [dataFieldIndex=" + dataFieldIndex + ", dataFieldIndexName=" + dataFieldIndexName
				+ ", dataFieldIndexType=" + dataFieldIndexType + ", dataFieldIndexLength=" + dataFieldIndexLength
				+ ", usefulInfoFlag=" + usefulInfoFlag + ", usefulInfoSize=" + usefulInfoSize + ", indexAttribute="
				+ indexAttribute + ", indexFlag=" + indexFlag + ", pickable=" + pickable + ", fieldOrder=" + fieldOrder
				+ "]";
	}

}
