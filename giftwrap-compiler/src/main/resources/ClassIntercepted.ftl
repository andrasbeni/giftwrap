package ${package};

import java.util.concurrent.Callable;
import com.github.andrasbeni.giftwrap.InterceptorCallable;

${visibility} class ${name} extends ${super} {

<#list interceptors as interceptor>
    private final ${interceptor.type} ${interceptor.name};
    
</#list>
<#list constructors as constructor>
    ${constructor.visibility} ${name}(<#list constructor.parameters as parameter>final ${parameter.type} ${parameter.name}, </#list><#list interceptors as interceptor>final ${interceptor.type} ${interceptor.name}<#sep>, </#sep></#list>) <#list constructor.exceptions as eT><#if eT?is_first>throws </#if>${eT}<#sep>, </#sep></#list> {
        super(<#list constructor.parameters as parameter>${parameter.name}<#sep>, </#sep></#list>);
        <#list interceptors as interceptor>
        this.${interceptor.name} = ${interceptor.name};
        </#list>
    }
    
</#list>
<#list methods as method>
    ${method.visibility} <#if method.returnType??>${method.returnType}<#else>void</#if> ${method.name}(<#list method.parameters as parameter>final ${parameter.type} ${parameter.name}<#sep>, </#sep></#list>) <#list method.exceptions as eT><#if eT?is_first>throws </#if>${eT}<#sep>, </#sep></#list> {
        Callable<Object> $c = new Callable<Object>() {
            public Object call() throws Exception {
                <#if method.returnType??>return </#if>${name}.super.${method.name}(<#list method.parameters as parameter>${parameter.name}<#sep>, </#sep></#list>);
                <#if method.returnType??><#else>return null;</#if>
            }
        };
        <#list method.annotations as annotation>
        <#assign annotationVar = "$a" + annotation_index >
        <#assign annotationImpl = annotation.type + "DefaultImplementation" >
        ${annotationImpl} ${annotationVar} = 
                new ${annotationImpl}();
        <#list annotation.properties as property>
        ${annotationVar}.${property.name}(${property.value});
        </#list>
        $c = new InterceptorCallable<${annotation.type}>(
                ${annotation.interceptorName}, ${annotationVar}, $c, "${method.name}", this<#list method.parameters as parameter>, ${parameter.name}</#list>);
        </#list>
        try {
            <#if method.returnType??>return (${method.returnType})</#if>$c.call();
        } catch (Exception $caught) {
            <#list method.exceptions as eT>
            if ($caught instanceof ${eT}) {
                throw (${eT}) $caught;
            }
            </#list>
            if ($caught instanceof RuntimeException) {
                throw (RuntimeException) $caught;
            }
            throw new RuntimeException($caught);
        }            
    }
    
</#list>
}
