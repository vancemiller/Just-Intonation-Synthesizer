package helpers;

import java.beans.PropertyChangeListener;

public interface PropertyChanger {
	public void addPropertyChangeListener(PropertyChangeListener l);

	public void removePropertyChangeListener(PropertyChangeListener l);

}
