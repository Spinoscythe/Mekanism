package mekanism.common.transmitters.grid;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import mekanism.api.heat.HeatAPI.HeatTransfer;
import mekanism.api.heat.IHeatHandler;
import mekanism.api.math.FloatingLong;
import mekanism.api.transmitters.DynamicNetwork;
import mekanism.api.transmitters.IGridTransmitter;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.heat.ITileHeatHandler;
import mekanism.common.transmitters.TransmitterImpl;
import mekanism.common.util.CapabilityUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit;
import net.minecraft.util.text.ITextComponent;

public class HeatNetwork extends DynamicNetwork<IHeatHandler, HeatNetwork, Void> {

    public FloatingLong meanTemp = FloatingLong.ZERO;

    public FloatingLong heatLost = FloatingLong.ZERO;
    public FloatingLong heatTransferred = FloatingLong.ZERO;

    public HeatNetwork() {
    }

    public HeatNetwork(UUID networkID) {
        super(networkID);
    }

    public HeatNetwork(Collection<HeatNetwork> networks) {
        for (HeatNetwork net : networks) {
            if (net != null) {
                adoptTransmittersAndAcceptorsFrom(net);
                net.deregister();
            }
        }
        register();
    }

    @Override
    public ITextComponent getNeededInfo() {
        return MekanismLang.NOT_APPLICABLE.translate();
    }

    @Override
    public ITextComponent getStoredInfo() {
        return MekanismLang.HEAT_NETWORK_STORED.translate(MekanismUtils.getTemperatureDisplay(meanTemp.doubleValue(), TemperatureUnit.KELVIN));
    }

    @Override
    public ITextComponent getFlowInfo() {
        ITextComponent transferred = MekanismUtils.getTemperatureDisplay(heatTransferred.doubleValue(), TemperatureUnit.KELVIN);
        ITextComponent lost = MekanismUtils.getTemperatureDisplay(heatLost.doubleValue(), TemperatureUnit.KELVIN);
        return heatTransferred.add(heatLost).isZero() ? MekanismLang.HEAT_NETWORK_FLOW.translate(transferred, lost)
                                               : MekanismLang.HEAT_NETWORK_FLOW_EFFICIENCY.translate(transferred, lost,
                                                   heatTransferred.divide(heatTransferred.add(heatLost)).multiply(100));
    }

    @Override
    public void absorbBuffer(IGridTransmitter<IHeatHandler, HeatNetwork, Void> transmitter) {
    }

    @Override
    public void clampBuffer() {
    }

    @Override
    protected synchronized void updateCapacity(IGridTransmitter<IHeatHandler, HeatNetwork, Void> transmitter) {
        //The capacity is always zero so no point in doing calculations.
    }

    @Override
    public synchronized void updateCapacity() {
        //The capacity is always zero so no point in doing calculations.
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        FloatingLong newSumTemp = FloatingLong.ZERO;
        FloatingLong newHeatLost = FloatingLong.ZERO;
        FloatingLong newHeatTransferred = FloatingLong.ZERO;

        if (!isRemote()) {
            for (IGridTransmitter<IHeatHandler, HeatNetwork, Void> transmitter : transmitters) {
                if (transmitter instanceof TransmitterImpl) {
                    Optional<IHeatHandler> capability = MekanismUtils.toOptional(CapabilityUtils.getCapability(((TransmitterImpl<?, ?, ?>) transmitter).getTileEntity(),
                          Capabilities.HEAT_HANDLER_CAPABILITY, null));
                    if (capability.isPresent()) {
                        IHeatHandler heatTransmitter = capability.get();
                        if (heatTransmitter instanceof ITileHeatHandler) {
                            ITileHeatHandler heatTile = (ITileHeatHandler) heatTransmitter;
                            HeatTransfer transfer = ((ITileHeatHandler) heatTransmitter).simulate();
                            heatTile.update(null);
                            newHeatTransferred = newHeatTransferred.plusEqual(transfer.getAdjacentTransfer());
                            newHeatLost = newHeatLost.plusEqual(transfer.getEnvironmentTransfer());
                            newSumTemp = newSumTemp.plusEqual(heatTile.getTotalTemperature());
                        }
                    }
                }
            }
        }
        heatLost = newHeatLost;
        heatTransferred = newHeatTransferred;
        meanTemp = newSumTemp.divide(transmitters.size());
    }

    @Override
    public String toString() {
        return "[HeatNetwork] " + transmitters.size() + " transmitters, " + possibleAcceptors.size() + " acceptors.";
    }

    @Override
    public ITextComponent getTextComponent() {
        return MekanismLang.NETWORK_DESCRIPTION.translate(MekanismLang.HEAT_NETWORK, transmitters.size(), possibleAcceptors.size());
    }
}