package se.stonepath.framework.magento.network;

import java.util.HashMap;

import se.stonepath.framework.magento.network.requests.InvoiceCreateRequest;
import se.stonepath.framework.magento.network.requests.LoginRequest;
import se.stonepath.framework.magento.network.requests.MagentoCall;
import se.stonepath.framework.magento.network.requests.OrderInfoRequest;
import se.stonepath.framework.magento.network.requests.ShipmentCreateRequest;
import se.stonepath.framework.rpcxml.XmlRpcConnection;
import se.stonepath.framework.rpcxml.XmlRpcException;
import se.stonepath.framework.rpcxml.XmlRpcRespond;
import se.stonepath.framework.rpcxml.respond.XmlRpcCollectionRespond;
import se.stonepath.framework.rpcxml.respond.value.XmlRpcStringRespond;


public class MagentoConnection extends XmlRpcConnection{

	public static final long serialVersionUID = 1L;
	
	private String sessionKey;
	
	private String username,password;
	
	public MagentoConnection(String connectionUrl) {
		super(connectionUrl + "/index.php/api/xmlrpc/");
		this.username = new String();
		this.password = new String();
	}
	public MagentoConnection(String connectionUrl,String username,String password) {
		super(connectionUrl + "/index.php/api/xmlrpc/");
		this.username = username;
		this.password = password;
	}
	
	
	public void login(String _username,String _password) throws Exception{
			XmlRpcStringRespond returnSessionKey = sendRequest(new LoginRequest(_username, _password), XmlRpcStringRespond.class);
			sessionKey = returnSessionKey.getValue();
	}
	public void login() throws Exception{
		XmlRpcStringRespond returnSessionKey = sendRequest(new LoginRequest(username, password), XmlRpcStringRespond.class);
		sessionKey = returnSessionKey.getValue();
	}
	
	
	public String invoiceOrder(int incrementId){
		return invoiceOrder(incrementId, "");
	}
	public String shipOrder(int incrementId){
		return shipOrder(incrementId, "");
	}
	
	public String invoiceOrder(int incrementId,String comment){
		
		OrderInfoRequest orderInfoRequest = new OrderInfoRequest(incrementId);
		XmlRpcCollectionRespond orderInfoRespond = call(orderInfoRequest,XmlRpcCollectionRespond.class);
		
		HashMap<Integer,Double> invoiceQty = new HashMap<Integer,Double>();
		
		for(Object itemObject : (Object[])orderInfoRespond.get("items")){
			@SuppressWarnings("unchecked")
			HashMap<String,Object> itemData = (HashMap<String,Object>)itemObject;
	
			invoiceQty.put(Integer.parseInt((String)itemData.get("item_id")), Double.parseDouble((String)itemData.get("qty_ordered")));	
		}
		
		InvoiceCreateRequest invoiceCreateRequest = new InvoiceCreateRequest(incrementId, invoiceQty,comment);
		XmlRpcStringRespond invoiceCreateRespond = call(invoiceCreateRequest,XmlRpcStringRespond.class);

		return invoiceCreateRespond.getValue();
	}
	
	
	public String shipOrder(int incrementId,String comment){
		OrderInfoRequest orderInfoRequest = new OrderInfoRequest(incrementId);
		XmlRpcCollectionRespond orderInfoRespond = call(orderInfoRequest,XmlRpcCollectionRespond.class);
		
		HashMap<Integer,Double> invoiceQty = new HashMap<Integer,Double>();
		
		for(Object itemObject : (Object[])orderInfoRespond.get("items")){
			@SuppressWarnings("unchecked")
			HashMap<String,Object> itemData = (HashMap<String,Object>)itemObject;
	
			invoiceQty.put(Integer.parseInt((String)itemData.get("item_id")), Double.parseDouble((String)itemData.get("qty_ordered")));	
		}
		
		ShipmentCreateRequest shipmentCreateRequest = new ShipmentCreateRequest(incrementId, invoiceQty,comment);
		XmlRpcStringRespond shipmentCreateRespond = call(shipmentCreateRequest,XmlRpcStringRespond.class);

		return shipmentCreateRespond.getValue();
	}
	
	public <T extends XmlRpcRespond> T call(MagentoCall call,Class<T> respond){
		
		try {
			call.prepareCall(sessionKey);
			return sendRequest(call,respond);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
}