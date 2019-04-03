package libFleur.core.transforms;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import libFleur.core.data.FCSDimension;
import libFleur.core.data.FCSFrame;
import libFleur.core.logging.LogFactory;
import libFleur.core.proto.TransformMapProto;
import libFleur.core.utils.TransformUtils;


public class TransformSet {
	
	Map<String, AbstractTransform> map;
	
	public TransformSet(){
		map = new HashMap<>();
	}
	
	public void addTransformEntry(String key, AbstractTransform value){
		map.put(key, value);
	}
	
	public void remove(String key){
		map.remove(key);
	}
	
	public void optimize(FCSFrame df){
		df.getDimensionNames().parallelStream().forEach(m -> optimizeTransform(m, df));
	}

	private void optimizeTransform(String name, FCSFrame df) {
		AbstractTransform t = map.get(name);
		FCSDimension dim = df.getDimension(name);
		if (t.getType().equals(TransformType.LOGICLE)){
			((LogicleTransform) t).optimize(df.getDimension(name).getData());
		} else if (t.getType().equals(TransformType.LOGARITHMIC)){
			((LogrithmicTransform) t).optimize(dim.getData());
		} else if (t.getType().equals(TransformType.BOUNDARY)){
			((BoundDisplayTransform) t).optimize(dim.getData());
		}
	}	
	
	public byte[] save(){
		TransformMapProto.TransformMap tBuilder = createMap();
		return tBuilder.toByteArray();
	}

	private TransformMapProto.TransformMap createMap() {
		TransformMapProto.TransformMap.Builder tBuilder = TransformMapProto.TransformMap.newBuilder();
		for (Entry<String, AbstractTransform> e:map.entrySet()){
			AbstractTransform at = e.getValue();
			TransformMapProto.TransformMap.TransformEntry.Builder entryBuilder = TransformMapProto.TransformMap.TransformEntry.newBuilder();
			entryBuilder.setKey(e.getKey());
			
			TransformMapProto.TransformMap.Transform.Builder transformBuilder = TransformMapProto.TransformMap.Transform.newBuilder();
			transformBuilder.setId(at.getID());
			if (at.getType().equals(TransformType.LOGICLE)){
				LogicleTransform lt = (LogicleTransform) at;
				transformBuilder.setType(TransformMapProto.TransformMap.TransformType.LOGICLE);
				transformBuilder.setLogicleT(lt.getT());
				transformBuilder.setLogicleW(lt.getW());
				transformBuilder.setLogicleM(lt.getM());
				transformBuilder.setLogicleA(lt.getA());
			} else if (at.getType().equals(TransformType.LOGARITHMIC)){
				LogrithmicTransform logT = (LogrithmicTransform) at;
				transformBuilder.setType(TransformMapProto.TransformMap.TransformType.LOG);
				transformBuilder.setLogMin(logT.getMin());
				transformBuilder.setLogMax(logT.getMax());
			} else if (at.getType().equals(TransformType.BOUNDARY)){
				BoundDisplayTransform bdt = (BoundDisplayTransform) at;
				transformBuilder.setType(TransformMapProto.TransformMap.TransformType.BOUNDARY);
				transformBuilder.setBoundMin(bdt.getMinRawValue());
				transformBuilder.setBoundMax(bdt.getMaxRawValue());
			}
			entryBuilder.setEntry(transformBuilder.build());
			tBuilder.addEntry(entryBuilder.build());
		}
		return tBuilder.build();
	}
	
	public static TransformSet load(byte[] bytes) throws InvalidProtocolBufferException{
		TransformSet s = new TransformSet();
		
		TransformMapProto.TransformMap tMap = TransformMapProto.TransformMap.parseFrom(bytes);
		
		for (int i=0;i<tMap.getEntryCount();i++){
			TransformMapProto.TransformMap.TransformEntry entry = tMap.getEntry(i);
			TransformMapProto.TransformMap.Transform serializedTransform = entry.getEntry();
			AbstractTransform loadedTransform = null;
			if (serializedTransform.getType().equals(TransformMapProto.TransformMap.TransformType.LOGICLE)){
				loadedTransform = new LogicleTransform(
						serializedTransform.getLogicleT(), 
						serializedTransform.getLogicleW(), 
						serializedTransform.getLogicleM(), 
						serializedTransform.getLogicleA());
			} else if (serializedTransform.getType().equals(TransformMapProto.TransformMap.TransformType.LOG)){
				loadedTransform = new LogrithmicTransform(serializedTransform.getLogMin(), serializedTransform.getLogMax());
			} else if (serializedTransform.getType().equals(TransformMapProto.TransformMap.TransformType.BOUNDARY)){
				loadedTransform = new BoundDisplayTransform(serializedTransform.getBoundMin(), serializedTransform.getBoundMax());
			}
			if (loadedTransform!=null){
				s.addTransformEntry(entry.getKey(), loadedTransform);
			}
		}	
		return s;
	}

	public AbstractTransform get(String shortName) {
		if (map.containsKey(shortName)){
			return map.get(shortName);
		} else {
			return TransformUtils.createDefaultTransform(shortName);
		}
	}

	public Map<String, AbstractTransform> getMap() {
		return map;
	}

	public String saveToString() {
		final TransformMapProto.TransformMap buffer = createMap();
		try {
			return JsonFormat.printer().print(buffer);
		} catch (InvalidProtocolBufferException e) {
			LogFactory.createLogger(this.getClass().getName()).log(Level.FINE, "Unable to serialize message to json.");
			return null;
		}
	}
	public static TransformSet loadFromProtoString(String previewString) throws InvalidProtocolBufferException {
		TransformMapProto.TransformMap.Builder mb = TransformMapProto.TransformMap.newBuilder();
		JsonFormat.parser().merge(previewString, mb);
		return TransformSet.load(mb.build().toByteArray());
	}

	public TransformSet deepCopy() {
		try {
			return TransformSet.load(this.save());
		} catch (InvalidProtocolBufferException e) {
			throw new RuntimeException("Unable to copy object.", e);
		}
	}
}