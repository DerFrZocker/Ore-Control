package de.derfrzocker.feature.common.value.number.integer;

import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Exp4jIntegerValue extends IntegerValue {

    // Dirty way to get the random into the function
    private final Map<Thread, Random> randoms = new ConcurrentHashMap<>();
    private String expressionString;
    private Expression expression;
    private boolean dirty = false;

    public Exp4jIntegerValue(String expressionString) {
        this.expressionString = expressionString;
        buildExpression();
    }

    private void buildExpression() {
        ExpressionBuilder builder = new ExpressionBuilder(expressionString);
        builder.variables("blockX", "blockY", "blockZ", "chunkX", "chunkZ");
        builder.function(new Function("randomDouble", 0) {
            @Override
            public double apply(double... doubles) {
                return randoms.get(Thread.currentThread()).nextDouble();
            }
        });
        builder.function(new Function("randomInt", 1) {
            @Override
            public double apply(double... doubles) {
                return randoms.get(Thread.currentThread()).nextInt((int) doubles[0]);
            }
        });
        this.expression = builder.build();
    }

    @Override
    public IntegerType getValueType() {
        return Exp4jIntegerType.INSTANCE;
    }

    @Override
    public Integer getValue(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion) {
        Expression expression = new Expression(this.expression);

        expression.setVariable("blockX", position.getBlockX());
        expression.setVariable("blockY", position.getBlockY());
        expression.setVariable("blockX", position.getBlockX());
        expression.setVariable("chunkX", (double) (position.getBlockX() / 16));
        expression.setVariable("chunkZ", (double) (position.getBlockZ() / 16));

        randoms.put(Thread.currentThread(), random);
        double result = expression.evaluate();
        randoms.remove(Thread.currentThread());

        return (int) result;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void saved() {
        dirty = false;
    }

    public String getExpressionString() {
        return expressionString;
    }

    public void setExpressionString(String expressionString) {
        this.expressionString = expressionString;
        buildExpression();
        dirty = true;
    }

    @Override
    public Exp4jIntegerValue clone() {
        return new Exp4jIntegerValue(expressionString);
    }
}
