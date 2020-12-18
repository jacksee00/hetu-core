/*
 * Copyright (C) 2018-2020. Huawei Technologies Co., Ltd. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.prestosql.operator;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.hetu.core.transport.execution.buffer.PagesSerde;
import io.prestosql.spi.Page;
import nove.hetu.executor.PageConsumer;

import javax.annotation.Nullable;

import java.net.URI;

import static java.util.Objects.requireNonNull;

/**
 * One client per location/split.
 * The client ensures the order of the page returned
 */
public class GrpcExchangeClient
        implements ExchangeClient
{
    private final PagesSerde pagesSerde;

    private PageConsumer pageConsumer;
    private boolean noMoreLocation;

    public GrpcExchangeClient(PagesSerde pagesSerde)
    {
        this.pagesSerde = requireNonNull(pagesSerde, "pagesSerde is null");
    }

    @Override
    public ExchangeClientStatus getStatistics()
    {
        return null;
    }

    @Override
    public void addLocation(URI location)
    {
        //location URI format: /v1/task/{taskId}/results/{bufferId}/{token} --> ["", "v1", "task",{taskid}, "result", {bufferid}]
        String[] paths = location.getPath().split("/");

//        getResult = ShuffleClient.getResults(
//                location.getHost(),
//                16544 /** BIG BIG HACK, ASSUME GRPC LISTEN ON THIS PORT, SHOULD CHANGE TO USE CONFIGURATION*/,
//                paths[3],
//                paths[5],
//                pageOutputBuffer);
        pageConsumer = new PageConsumer(paths[3], paths[5], pagesSerde);
    }

    @Override
    public void setNoMoreLocation()
    {
        noMoreLocation = true;
    }

    @Nullable
    @Override
    public Page pollPage()
    {
        if (pageConsumer == null) {
            return null;
        }
        return pageConsumer.poll();
    }

    @Override
    public boolean isFinished()
    {
        return pageConsumer != null && pageConsumer.isEnded();
    }

    @Override
    public boolean isClosed()
    {
        /** shouldn't need to check if pageConsumer is null if this is only called after {@link #addLocation(URI)} is invoked */
        return pageConsumer != null && pageConsumer.isEnded();
    }

    @Override
    public ListenableFuture<?> isBlocked()
    {
        return Futures.immediateFuture(true);
//        if (isClosed()) {
//            return Futures.immediateFuture(true);
//        }
//        return SettableFuture.create();
    }

    @Override
    public void close() {}
}
