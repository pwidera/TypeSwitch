package ninja.programista.typeswitch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TypeSwitchBuilder implements TypeSwitch {

    private class TypeMatch<T> {
        private Class<T> aClass;
        private Consumer<T> aConsumer;

        public TypeMatch(Class<T> aClass, Consumer<T> aConsumer) {
            this.aClass = aClass;
            this.aConsumer = aConsumer;
        }
    }

    // ten obiekt moze byc lista, setem lub mapa w zaleznosci od zachowania ktore chcemy uzyskac
    private List<TypeMatch> matchersList = new ArrayList<>();
    private Optional<Consumer> onMismath = Optional.empty();
    private Optional<Consumer<Exception>> onError = Optional.empty();
    private Optional<BiConsumer<Object, Long>> onLog = Optional.empty();

    public static TypeSwitchBuilder getInstance() {
        return new TypeSwitchBuilder();
    }

    public <T> TypeSwitchBuilder with(final Class<T> targetClass, final Consumer<T> consumer) {
        matchersList.add(new TypeMatch(targetClass, consumer));
        return this;
    }

    public <T> TypeSwitchBuilder withPerfLog(final BiConsumer<Object, Long> consumer) {
        onLog = Optional.of(consumer);
        return this;
    }

    public TypeSwitchBuilder onError(final Consumer<Exception> consumer){
        this.onError = Optional.of(consumer);
        return this;
    }

    public TypeSwitchBuilder onMismath(final Consumer<Object> consumer) {
        this.onMismath = Optional.of(consumer);
        return this;
    }

    public TypeSwitch build() {
        return this;
    }

    public void handle(Object o) {
        List<TypeMatch> collect = matchersList.stream().filter(match -> match.aClass.equals(o.getClass())).collect(Collectors.toList());
        if(collect.size()==0) {
            onMismath.ifPresent( mis -> mis.accept(o));
        } else {
            try {
                collect.forEach(match -> {
                    long start = System.currentTimeMillis();
                    match.aConsumer.accept(o);
                    onLog.ifPresent(log -> log.accept(o, System.currentTimeMillis()-start));
                });
            } catch (Exception e) {
                onError.ifPresent( err -> err.accept(e));
            }
        }
    }
}

interface TypeSwitch {
    void handle(Object o);
}